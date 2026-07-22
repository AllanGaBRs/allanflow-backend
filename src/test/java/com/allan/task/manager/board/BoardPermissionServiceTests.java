package com.allan.task.manager.board;

import com.allan.task.manager.board.exceptions.BoardAccessDeniedException;
import com.allan.task.manager.membership.MembershipModel;
import com.allan.task.manager.workspace.WorkspacePermissionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BoardPermissionServiceTests {

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private WorkspacePermissionService workspacePermissionService;

    @InjectMocks
    private BoardPermissionService boardPermissionService;

    @Test
    void requireBoardAccessShouldAllowWorkspaceOwnerWithoutCheckingBoardMembers() {
        UUID workspaceId = UUID.randomUUID();
        UUID boardId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        MembershipModel membership = new MembershipModel();
        membership.setRole(MembershipModel.MembershipRole.OWNER);

        when(workspacePermissionService.requireMember(workspaceId, userId))
                .thenReturn(membership);

        boardPermissionService.requireBoardAccess(workspaceId, boardId, userId);

        verify(boardRepository, never())
                .existsByIdAndWorkspaceIdAndMembersId(boardId, workspaceId, userId);
    }

    @Test
    void requireBoardAccessShouldAllowWorkspaceAdminWithoutCheckingBoardMembers() {
        UUID workspaceId = UUID.randomUUID();
        UUID boardId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        MembershipModel membership = new MembershipModel();
        membership.setRole(MembershipModel.MembershipRole.ADMIN);

        when(workspacePermissionService.requireMember(workspaceId, userId))
                .thenReturn(membership);

        boardPermissionService.requireBoardAccess(workspaceId, boardId, userId);

        verify(boardRepository, never())
                .existsByIdAndWorkspaceIdAndMembersId(boardId, workspaceId, userId);
    }

    @Test
    void requireBoardAccessShouldAllowMemberWhenUserBelongsToBoard() {
        UUID workspaceId = UUID.randomUUID();
        UUID boardId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        MembershipModel membership = new MembershipModel();
        membership.setRole(MembershipModel.MembershipRole.MEMBER);

        when(workspacePermissionService.requireMember(workspaceId, userId))
                .thenReturn(membership);

        when(boardRepository.existsByIdAndWorkspaceIdAndMembersId(boardId, workspaceId, userId))
                .thenReturn(true);

        boardPermissionService.requireBoardAccess(workspaceId, boardId, userId);
    }

    @Test
    void requireBoardAccessShouldThrowExceptionWhenMemberDoesNotBelongToBoard() {
        UUID workspaceId = UUID.randomUUID();
        UUID boardId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        MembershipModel membership = new MembershipModel();
        membership.setRole(MembershipModel.MembershipRole.MEMBER);

        when(workspacePermissionService.requireMember(workspaceId, userId))
                .thenReturn(membership);

        when(boardRepository.existsByIdAndWorkspaceIdAndMembersId(boardId, workspaceId, userId))
                .thenReturn(false);

        assertThrows(
                BoardAccessDeniedException.class,
                () -> boardPermissionService.requireBoardAccess(workspaceId, boardId, userId)
        );
    }

    @Test
    void requireBoardManagementShouldDelegateToWorkspaceOwnerOrAdminPermission() {
        UUID workspaceId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        boardPermissionService.requireBoardManagement(workspaceId, userId);

        verify(workspacePermissionService)
                .requireOwnerOrAdmin(workspaceId, userId);
    }
}