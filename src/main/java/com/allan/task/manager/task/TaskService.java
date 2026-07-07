package com.allan.task.manager.task;

import com.allan.task.manager.column.ColumnLookupService;
import com.allan.task.manager.column.ColumnModel;
import com.allan.task.manager.label.LabelLookupService;
import com.allan.task.manager.label.LabelModel;
import com.allan.task.manager.task.dto.TaskCreateDTO;
import com.allan.task.manager.task.dto.TaskMoveDTO;
import com.allan.task.manager.task.dto.TaskResponseDTO;
import com.allan.task.manager.task.mapper.TaskMapper;
import com.allan.task.manager.user.UserLookupService;
import com.allan.task.manager.user.UserModel;
import com.allan.task.manager.workspace.WorkspacePermissionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserLookupService userLookupService;
    private final LabelLookupService labelLookupService;
    private final ColumnLookupService columnLookupService;
    private final WorkspacePermissionService workspacePermissionService;
    private final TaskLookupService taskLookupService;

    public TaskService(
            TaskRepository taskRepository,
            UserLookupService userLookupService,
            LabelLookupService labelLookupService,
            ColumnLookupService columnLookupService,
            WorkspacePermissionService workspacePermissionService,
            TaskLookupService taskLookupService
    ) {
        this.taskRepository = taskRepository;
        this.userLookupService = userLookupService;
        this.labelLookupService = labelLookupService;
        this.columnLookupService = columnLookupService;
        this.workspacePermissionService = workspacePermissionService;
        this.taskLookupService = taskLookupService;
    }

    @Transactional
    public TaskResponseDTO create(
            TaskCreateDTO dto,
            UUID workspaceId,
            UUID boardId,
            UUID columnId,
            UUID requesterId
    ) {
        workspacePermissionService.requireOwnerOrAdmin(workspaceId, requesterId);

        ColumnModel column =
                columnLookupService.findColumnInBoard(workspaceId, boardId, columnId);

        List<UserModel> assignees =
                userLookupService.findAllByIds(dto.assignees());

        List<LabelModel> labels =
                labelLookupService.findAllInBoard(boardId, dto.labels());

        TaskModel task = new TaskModel();

        task.setTitle(dto.title());
        task.setDescription(dto.description());

        task.setWorkspace(column.getWorkspace());
        task.setBoard(column.getBoard());
        task.setColumn(column);

        task.setPosition(taskLookupService.resolvePosition(columnId));

        task.setPriority(
                dto.priority() != null
                        ? dto.priority()
                        : TaskModel.Priority.MEDIUM
        );

        task.setDueDate(dto.dueDate());

        task.setArchived(false);

        task.setAssignees(new HashSet<>(assignees));
        task.setLabels(new HashSet<>(labels));

        TaskModel saved = taskRepository.save(task);

        return TaskMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public TaskResponseDTO findById(
            UUID workspaceId,
            UUID boardId,
            UUID columnId,
            UUID taskId,
            UUID requesterId
    ) {
        workspacePermissionService.requireMember(workspaceId, requesterId);

        columnLookupService.findColumnInBoard(workspaceId, boardId, columnId);

        TaskModel task = taskLookupService.findTaskInColumn(columnId, taskId);

        return TaskMapper.toResponse(task);
    }

    @Transactional
    public void delete(
            UUID workspaceId,
            UUID boardId,
            UUID columnId,
            UUID taskId,
            UUID requesterId
    ) {
        workspacePermissionService.requireOwnerOrAdmin(workspaceId, requesterId);

        columnLookupService.findColumnInBoard(workspaceId, boardId, columnId);

        TaskModel task = taskLookupService.findTaskInColumn(columnId, taskId);

        taskRepository.delete(task);
    }

    @Transactional(readOnly = true)
    public List<TaskResponseDTO> findAllByColumn(
            UUID workspaceId,
            UUID boardId,
            UUID columnId,
            UUID requesterId
    ) {
        workspacePermissionService.requireMember(workspaceId, requesterId);

        columnLookupService.findColumnInBoard(workspaceId, boardId, columnId);

        List<TaskModel> tasks =
                taskRepository.findAllByColumnIdOrderByPositionAsc(columnId);

        return TaskMapper.toResponseList(tasks);
    }

    //TODO: Implement task reordering when drag-and-drop support is added.
    @Transactional
    public TaskResponseDTO moveTask(
            UUID workspaceId,
            UUID boardId,
            UUID sourceColumnId,
            UUID taskId,
            TaskMoveDTO dto,
            UUID requesterId
    ){
        workspacePermissionService.requireMember(workspaceId, requesterId);

        columnLookupService.findColumnInBoard(workspaceId, boardId, sourceColumnId);
        ColumnModel targetColumn = columnLookupService.findColumnInBoard(workspaceId, boardId, dto.targetColumnId());

        TaskModel task = taskLookupService.findTaskInColumn(sourceColumnId, taskId);

        //TODO: Remove this early return when task reordering within the same column is supported.
        if (sourceColumnId.equals(dto.targetColumnId())) {
            return TaskMapper.toResponse(task);
        }

        task.setColumn(targetColumn);
        task.setPosition(
                taskLookupService.resolvePosition(dto.targetColumnId())
        );

        task = taskRepository.save(task);
        return TaskMapper.toResponse(task);
    }
}