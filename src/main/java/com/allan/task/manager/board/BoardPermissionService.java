package com.allan.task.manager.board;

import com.allan.task.manager.board.exceptions.BoardAccessDeniedException;
import com.allan.task.manager.membership.MembershipModel;
import com.allan.task.manager.workspace.WorkspacePermissionService;
import com.allan.task.manager.workspace.exception.WorkspaceAccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class BoardPermissionService {

    private final BoardRepository boardRepository;
    private final WorkspacePermissionService workspacePermissionService;

    public BoardPermissionService(
            BoardRepository boardRepository,
            WorkspacePermissionService workspacePermissionService
    ) {
        this.boardRepository = boardRepository;
        this.workspacePermissionService = workspacePermissionService;
    }

    public void requireBoardAccess(UUID workspaceId, UUID boardId, UUID userId) {
        MembershipModel membership = workspacePermissionService.requireMember(workspaceId, userId);

        if (isWorkspaceOwnerOrAdmin(membership)) {
            return;
        }

        boolean isBoardMember = boardRepository.existsByIdAndWorkspaceIdAndMembersId(
                boardId,
                workspaceId,
                userId
        );

        if (!isBoardMember) {
            throw new BoardAccessDeniedException("You do not have access to this board");
        }
    }

    public void requireBoardManagement(UUID workspaceId, UUID userId) {
        workspacePermissionService.requireOwnerOrAdmin(workspaceId, userId);
    }

    private boolean isWorkspaceOwnerOrAdmin(MembershipModel membership) {
        return membership.getRole() == MembershipModel.MembershipRole.OWNER
                || membership.getRole() == MembershipModel.MembershipRole.ADMIN;
    }
}
