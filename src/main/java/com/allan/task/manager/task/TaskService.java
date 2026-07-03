package com.allan.task.manager.task;

import com.allan.task.manager.column.ColumnLookupService;
import com.allan.task.manager.column.ColumnModel;
import com.allan.task.manager.label.LabelModel;
import com.allan.task.manager.label.LabelRepository;
import com.allan.task.manager.task.dto.TaskCreateDTO;
import com.allan.task.manager.task.dto.TaskResponseDTO;
import com.allan.task.manager.task.mapper.TaskMapper;
import com.allan.task.manager.user.UserModel;
import com.allan.task.manager.user.UserRepository;
import com.allan.task.manager.workspace.WorkspacePermissionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final LabelRepository labelRepository;
    private final ColumnLookupService columnLookupService;
    private final WorkspacePermissionService workspacePermissionService;
    private final TaskLookupService taskLookupService;

    public TaskService(TaskRepository taskRepository,
                       UserRepository userRepository,
                       LabelRepository labelRepository,
                       ColumnLookupService columnLookupService,
                       WorkspacePermissionService workspacePermissionService,
                       TaskLookupService taskLookupService) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.labelRepository = labelRepository;
        this.columnLookupService = columnLookupService;
        this.workspacePermissionService = workspacePermissionService;
        this.taskLookupService = taskLookupService;
    }

    @Transactional
    public TaskResponseDTO create(
            TaskCreateDTO dto,
            UUID workspaceId,
            UUID columnId,
            UUID requesterId,
            UUID boardId
    ) {

        workspacePermissionService.requireOwnerOrAdmin(workspaceId, requesterId);

        ColumnModel column =
                columnLookupService.findColumnInBoard(workspaceId, boardId, columnId);

        List<UserModel> assignees =
                dto.assignees() != null
                        ? userRepository.findAllById(dto.assignees())
                        : List.of();

        List<LabelModel> labels =
                dto.labels() != null
                        ? labelRepository.findAllById(dto.labels())
                        : List.of();

        TaskModel task = new TaskModel();

        task.setTitle(dto.title());
        task.setDescription(dto.description());

        task.setWorkspace(column.getWorkspace());
        task.setBoard(column.getBoard());
        task.setColumn(column);

        task.setPosition(taskLookupService.resolvePosition(columnId));

        task.setArchived(false);

        task.setPriority(
                dto.priority() != null
                        ? dto.priority()
                        : TaskModel.Priority.MEDIUM
        );

        task.setDueDate(dto.dueDate());

        task.setAssignees(new HashSet<>(assignees));
        task.setLabels(new HashSet<>(labels));

        TaskModel saved = taskRepository.save(task);

        return TaskMapper.toResponse(saved);
    }
}