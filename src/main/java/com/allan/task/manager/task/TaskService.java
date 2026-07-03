package com.allan.task.manager.task;

import com.allan.task.manager.board.BoardLookupService;
import com.allan.task.manager.board.BoardModel;
import com.allan.task.manager.board.BoardRepository;
import com.allan.task.manager.column.ColumnLookupService;
import com.allan.task.manager.column.ColumnModel;
import com.allan.task.manager.label.LabelModel;
import com.allan.task.manager.label.LabelRepository;
import com.allan.task.manager.task.dto.TaskCreateDTO;
import com.allan.task.manager.task.dto.TaskResponseDTO;
import com.allan.task.manager.user.UserLookupService;
import com.allan.task.manager.user.UserModel;
import com.allan.task.manager.user.UserRepository;
import com.allan.task.manager.workspace.WorkspacePermissionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final LabelRepository labelRepository;
    private final UserLookupService userLookupService;
    private final ColumnLookupService columnLookupService;
    private final WorkspacePermissionService workspacePermissionService;

    public TaskService(TaskRepository taskRepository,
                       UserRepository userRepository,
                       LabelRepository labelRepository,
                       UserLookupService userLookupService,
                       ColumnLookupService columnLookupService,
                       WorkspacePermissionService workspacePermissionService) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.labelRepository = labelRepository;
        this.userLookupService = userLookupService;
        this.columnLookupService = columnLookupService;
        this.workspacePermissionService = workspacePermissionService;
    }

    /*@Transactional
    public TaskResponseDTO create(
            TaskCreateDTO dto,
            UUID workspaceId,
            UUID columnId,
            UUID requesterId,
            UUID boardId
    ){
        workspacePermissionService.requireOwnerOrAdmin(workspaceId, requesterId);

        ColumnModel column = columnLookupService.findColumnInBoard(workspaceId, boardId, columnId);

        List<UserModel> assignees = dto.assignees()  != null
                ? userRepository.findAllById(dto.assignees())
                : List.of();

        List<LabelModel> tags = dto.labels() != null
                ? labelRepository.findAllById(dto.labels())
                : List.of();

        TaskModel task = new TaskModel();
        task.setTitle(dto.title());
        task.setDescription(dto.description());

        if(dto.assignees().toArray().length > 0){

        }
    }*/
}
