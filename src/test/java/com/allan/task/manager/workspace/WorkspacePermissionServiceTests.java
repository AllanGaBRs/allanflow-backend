package com.allan.task.manager.workspace;

import com.allan.task.manager.membership.MembershipModel;
import com.allan.task.manager.membership.MembershipRepository;
import com.allan.task.manager.workspace.exception.WorkspaceAccessDeniedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkspacePermissionServiceTests {

    @Mock
    private MembershipRepository membershipRepository;

    @InjectMocks
    private WorkspacePermissionService workspacePermissionService;

    @Test
    void requireMemberShouldReturnMembershipWhenUserIsMember() {
        UUID workspaceId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        MembershipModel membership = new MembershipModel();

        when(membershipRepository.findByWorkspaceIdAndUserId(workspaceId, userId))
                .thenReturn(Optional.of(membership));

        MembershipModel result = workspacePermissionService.requireMember(workspaceId, userId);

        assertSame(membership, result);
    }

    @Test
    void requireMemberShouldThrowExceptionWhenUserIsNotMember() {
        UUID workspaceId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(membershipRepository.findByWorkspaceIdAndUserId(workspaceId, userId))
                .thenReturn(Optional.empty());

        assertThrows(
                WorkspaceAccessDeniedException.class,
                () -> workspacePermissionService.requireMember(workspaceId, userId)
        );
    }

    @Test
    void requireOwnerShouldThrowExceptionWhenUserIsMember() {
        UUID workspaceId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        MembershipModel membership = new MembershipModel();
        membership.setRole(MembershipModel.MembershipRole.MEMBER);

        when(membershipRepository.findByWorkspaceIdAndUserId(workspaceId, userId))
                .thenReturn(Optional.of(membership));

        assertThrows(
                WorkspaceAccessDeniedException.class,
                () -> workspacePermissionService.requireOwner(workspaceId, userId)
        );
    }

    @Test
    void requireOwnerOrAdminShouldReturnMembershipWhenUserIsAdmin() {
        UUID workspaceId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        MembershipModel membership = new MembershipModel();
        membership.setRole(MembershipModel.MembershipRole.ADMIN);

        when(membershipRepository.findByWorkspaceIdAndUserId(workspaceId, userId))
                .thenReturn(Optional.of(membership));

        MembershipModel result = workspacePermissionService.requireOwnerOrAdmin(workspaceId, userId);

        assertSame(membership, result);
    }
}