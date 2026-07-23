package com.allan.task.manager.membership;

import com.allan.task.manager.factory.Factory;
import com.allan.task.manager.membership.dto.MembershipCreateDTO;
import com.allan.task.manager.membership.dto.MembershipUpdateRoleDTO;
import com.allan.task.manager.membership.exception.MembershipAlreadyExistsException;
import com.allan.task.manager.user.UserLookupService;
import com.allan.task.manager.user.UserModel;
import com.allan.task.manager.workspace.WorkspaceLookupService;
import com.allan.task.manager.workspace.WorkspaceModel;
import com.allan.task.manager.workspace.WorkspacePermissionService;
import com.allan.task.manager.workspace.exception.WorkspaceAccessDeniedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MembershipServiceTests {

    @Mock
    private MembershipRepository membershipRepository;

    @Mock
    private UserLookupService userLookupService;

    @Mock
    private WorkspaceLookupService workspaceLookupService;

    @Mock
    private WorkspacePermissionService workspacePermissionService;

    @Mock
    private MembershipLookupService membershipLookupService;

    @InjectMocks
    private MembershipService membershipService;

    @Test
    void addMemberShouldThrowExceptionWhenUserIsAlreadyMember() {
        UUID workspaceId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        UserModel userToAdd = Factory.createUserModel();
        WorkspaceModel workspace = Factory.createWorkspaceModel();
        MembershipCreateDTO dto = new MembershipCreateDTO(
                userToAdd.getEmail(),
                MembershipModel.MembershipRole.MEMBER
        );

        when(workspaceLookupService.findActiveById(workspaceId))
                .thenReturn(workspace);
        when(userLookupService.findActiveByEmail(dto.email()))
                .thenReturn(userToAdd);
        when(membershipRepository.existsByWorkspaceIdAndUserId(
                workspaceId,
                userToAdd.getId()
        )).thenReturn(true);

        assertThrows(
                MembershipAlreadyExistsException.class,
                () -> membershipService.addMember(workspaceId, dto, requesterId)
        );

        verify(membershipRepository, never()).save(any());
    }

    @Test
    void addMemberShouldThrowExceptionWhenRoleIsOwner() {
        UUID workspaceId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        UserModel userToAdd = Factory.createUserModel();
        WorkspaceModel workspace = Factory.createWorkspaceModel();
        MembershipCreateDTO dto = new MembershipCreateDTO(
                userToAdd.getEmail(),
                MembershipModel.MembershipRole.OWNER
        );

        when(workspaceLookupService.findActiveById(workspaceId))
                .thenReturn(workspace);
        when(userLookupService.findActiveByEmail(dto.email()))
                .thenReturn(userToAdd);
        when(membershipRepository.existsByWorkspaceIdAndUserId(
                workspaceId,
                userToAdd.getId()
        )).thenReturn(false);

        assertThrows(
                WorkspaceAccessDeniedException.class,
                () -> membershipService.addMember(workspaceId, dto, requesterId)
        );

        verify(membershipRepository, never()).save(any());
    }

    @Test
    void updateRoleShouldThrowExceptionWhenTargetIsOwner() {
        UUID workspaceId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        MembershipModel ownerMembership = Factory.createMembershipModel(
                MembershipModel.MembershipRole.OWNER
        );
        UUID targetUserId = ownerMembership.getUser().getId();
        MembershipUpdateRoleDTO dto = new MembershipUpdateRoleDTO(
                MembershipModel.MembershipRole.ADMIN
        );

        when(membershipLookupService.findByWorkspaceAndUser(
                workspaceId,
                targetUserId
        )).thenReturn(ownerMembership);

        assertThrows(
                WorkspaceAccessDeniedException.class,
                () -> membershipService.updateRole(
                        workspaceId,
                        targetUserId,
                        dto,
                        requesterId
                )
        );

        verify(membershipRepository, never()).save(any());
    }

    @Test
    void removeMemberShouldThrowExceptionWhenRequesterRemovesItself() {
        UUID workspaceId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        MembershipModel membership = Factory.createMembershipModel(
                MembershipModel.MembershipRole.ADMIN
        );

        when(membershipLookupService.findByWorkspaceAndUser(
                workspaceId,
                requesterId
        )).thenReturn(membership);

        assertThrows(
                WorkspaceAccessDeniedException.class,
                () -> membershipService.removeMember(
                        workspaceId,
                        requesterId,
                        requesterId
                )
        );

        verify(membershipRepository, never()).delete(any());
    }
}
