package com.allan.task.manager.dashboard;

import com.allan.task.manager.board.BoardRepository;
import com.allan.task.manager.dashboard.dto.DashboardResponseDTO;
import com.allan.task.manager.dashboard.dto.DashboardSummaryDTO;
import com.allan.task.manager.dashboard.dto.TasksByPriorityDTO;
import com.allan.task.manager.membership.MembershipRepository;
import com.allan.task.manager.task.TaskModel;
import com.allan.task.manager.task.TaskRepository;
import com.allan.task.manager.workspace.WorkspacePermissionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class DashboardService {

    private final BoardRepository boardRepository;
    private final TaskRepository taskRepository;
    private final MembershipRepository membershipRepository;
    private final WorkspacePermissionService workspacePermissionService;

    public DashboardService(
            BoardRepository boardRepository,
            TaskRepository taskRepository,
            MembershipRepository membershipRepository,
            WorkspacePermissionService workspacePermissionService
    ) {
        this.boardRepository = boardRepository;
        this.taskRepository = taskRepository;
        this.membershipRepository = membershipRepository;
        this.workspacePermissionService = workspacePermissionService;
    }

    @Transactional(readOnly = true)
    public DashboardResponseDTO find(UUID workspaceId, UUID requesterId) {
        workspacePermissionService.requireOwnerOrAdmin(workspaceId, requesterId);

        DashboardSummaryDTO summary = new DashboardSummaryDTO(
                boardRepository.countByWorkspaceId(workspaceId),
                taskRepository.countByWorkspaceIdAndArchivedFalse(workspaceId),
                taskRepository.countOverdueByWorkspaceId(workspaceId, LocalDateTime.now()),
                membershipRepository.countByWorkspaceId(workspaceId)
        );

        TasksByPriorityDTO tasksByPriority = new TasksByPriorityDTO(
                countTasksByPriority(workspaceId, TaskModel.Priority.LOW),
                countTasksByPriority(workspaceId, TaskModel.Priority.MEDIUM),
                countTasksByPriority(workspaceId, TaskModel.Priority.HIGH)
        );

        return new DashboardResponseDTO(summary, tasksByPriority);
    }

    private long countTasksByPriority(UUID workspaceId, TaskModel.Priority priority) {
        return taskRepository.countByWorkspaceIdAndArchivedFalseAndPriority(
                workspaceId,
                priority
        );
    }
}
