package com.allan.task.manager.membership;

import com.allan.task.manager.membership.dto.MembershipCreateDTO;
import com.allan.task.manager.membership.dto.MembershipResponseDTO;
import com.allan.task.manager.membership.dto.MembershipUpdateRoleDTO;
import com.allan.task.manager.membership.exception.MembershipAlreadyExistsException;
import com.allan.task.manager.membership.mapper.MembershipMapper;
import com.allan.task.manager.user.UserModel;
import com.allan.task.manager.user.UserLookupService;
import com.allan.task.manager.workspace.WorkspaceModel;
import com.allan.task.manager.workspace.WorkspaceLookupService;
import com.allan.task.manager.workspace.WorkspacePermissionService;
import com.allan.task.manager.workspace.exception.WorkspaceAccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class MembershipService {

    private final MembershipRepository membershipRepository;
    private final UserLookupService userLookupService;
    private final WorkspaceLookupService workspaceLookupService;
    private final WorkspacePermissionService workspacePermissionService;
    private final MembershipLookupService membershipLookupService;

    public MembershipService(
            MembershipRepository membershipRepository,
            UserLookupService userLookupService,
            WorkspaceLookupService workspaceLookupService,
            WorkspacePermissionService workspacePermissionService,
            MembershipLookupService membershipLookupService
    ) {
        this.membershipRepository = membershipRepository;
        this.userLookupService = userLookupService;
        this.workspaceLookupService = workspaceLookupService;
        this.workspacePermissionService = workspacePermissionService;
        this.membershipLookupService = membershipLookupService;
    }

    @Transactional
    public MembershipResponseDTO addMember(
            UUID workspaceId,
            MembershipCreateDTO dto,
            UUID requesterId
    ) {
        workspacePermissionService.requireOwnerOrAdmin(workspaceId, requesterId);

        WorkspaceModel workspace = workspaceLookupService.findActiveById(workspaceId);

        UserModel userToAdd = userLookupService.findActiveByEmail(dto.email());

        if (membershipRepository.existsByWorkspaceIdAndUserId(workspaceId, userToAdd.getId())) {
            throw new MembershipAlreadyExistsException("User is already a member of this workspace");
        }

        if (dto.role() == MembershipModel.MembershipRole.OWNER) {
            throw new WorkspaceAccessDeniedException("Cannot add another owner to the workspace");
        }

        MembershipModel membership = new MembershipModel();
        membership.setWorkspace(workspace);
        membership.setUser(userToAdd);
        membership.setRole(dto.role());

        MembershipModel savedMembership = membershipRepository.save(membership);

        return MembershipMapper.toResponse(savedMembership);
    }

    @Transactional(readOnly = true)
    public List<MembershipResponseDTO> findMembers(
            UUID workspaceId,
            UUID requesterId
    ) {
        workspacePermissionService.requireMember(workspaceId, requesterId);

        return MembershipMapper.toResponseList(
                membershipRepository.findByWorkspaceId(workspaceId)
        );
    }

    @Transactional
    public MembershipResponseDTO updateRole(
            UUID workspaceId,
            UUID targetUserId,
            MembershipUpdateRoleDTO dto,
            UUID requesterId
    ) {
        workspacePermissionService.requireOwnerOrAdmin(workspaceId, requesterId);

        MembershipModel targetMembership = membershipLookupService.findByWorkspaceAndUser(workspaceId, targetUserId);

        if (targetMembership.getRole() == MembershipModel.MembershipRole.OWNER) {
            throw new WorkspaceAccessDeniedException("Cannot change owner role");
        }

        if (dto.role() == MembershipModel.MembershipRole.OWNER) {
            throw new WorkspaceAccessDeniedException("Cannot promote member to owner");
        }

        targetMembership.setRole(dto.role());

        return MembershipMapper.toResponse(membershipRepository.save(targetMembership));
    }

    @Transactional
    public void removeMember(
            UUID workspaceId,
            UUID targetUserId,
            UUID requesterId
    ) {
        workspacePermissionService.requireOwnerOrAdmin(workspaceId, requesterId);

        MembershipModel targetMembership = membershipLookupService.findByWorkspaceAndUser(workspaceId, targetUserId);

        if (targetMembership.getRole() == MembershipModel.MembershipRole.OWNER) {
            throw new WorkspaceAccessDeniedException("Cannot remove workspace owner");
        }

        if (requesterId.equals(targetUserId)) {
            throw new WorkspaceAccessDeniedException("You cannot remove yourself from the workspace");
        }

        membershipRepository.delete(targetMembership);
    }
}
