package com.allan.task.manager.membership;

import com.allan.task.manager.membership.dto.MembershipCreateDTO;
import com.allan.task.manager.membership.dto.MembershipResponseDTO;
import com.allan.task.manager.membership.dto.MembershipUpdateRoleDTO;
import com.allan.task.manager.membership.exception.MembershipAlreadyExistsException;
import com.allan.task.manager.membership.exception.MembershipNotFoundException;
import com.allan.task.manager.membership.mapper.MembershipMapper;
import com.allan.task.manager.user.UserModel;
import com.allan.task.manager.user.UserRepository;
import com.allan.task.manager.user.exception.UserNotFoundException;
import com.allan.task.manager.workspace.WorkspaceModel;
import com.allan.task.manager.workspace.WorkspaceRepository;
import com.allan.task.manager.workspace.exception.WorkspaceAccessDeniedException;
import com.allan.task.manager.workspace.exception.WorkspaceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class MembershipService {

    private final MembershipRepository membershipRepository;
    private final UserRepository userRepository;
    private final WorkspaceRepository workspaceRepository;

    public MembershipService(
            MembershipRepository membershipRepository,
            UserRepository userRepository,
            WorkspaceRepository workspaceRepository
    ) {
        this.membershipRepository = membershipRepository;
        this.userRepository = userRepository;
        this.workspaceRepository = workspaceRepository;
    }

    @Transactional
    public MembershipResponseDTO addMember(
            UUID workspaceId,
            MembershipCreateDTO dto,
            UUID requesterId
    ) {
        MembershipModel requesterMembership = findMembership(workspaceId, requesterId);
        validateOwnerOrAdmin(requesterMembership);

        WorkspaceModel workspace = findActiveWorkspace(workspaceId);

        UserModel userToAdd = userRepository.findByEmailAndIsActiveTrue(dto.email())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

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
        findMembership(workspaceId, requesterId);

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
        MembershipModel requesterMembership = findMembership(workspaceId, requesterId);
        validateOwnerOrAdmin(requesterMembership);

        MembershipModel targetMembership = findMembership(workspaceId, targetUserId);

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
        MembershipModel requesterMembership = findMembership(workspaceId, requesterId);
        validateOwnerOrAdmin(requesterMembership);

        MembershipModel targetMembership = findMembership(workspaceId, targetUserId);

        if (targetMembership.getRole() == MembershipModel.MembershipRole.OWNER) {
            throw new WorkspaceAccessDeniedException("Cannot remove workspace owner");
        }

        if (requesterId.equals(targetUserId)) {
            throw new WorkspaceAccessDeniedException("You cannot remove yourself from the workspace");
        }

        membershipRepository.delete(targetMembership);
    }

    private WorkspaceModel findActiveWorkspace(UUID workspaceId) {
        return workspaceRepository.findByIdAndIsActiveTrue(workspaceId)
                .orElseThrow(() -> new WorkspaceNotFoundException("Workspace not found"));
    }

    private MembershipModel findMembership(UUID workspaceId, UUID userId) {
        return membershipRepository.findByWorkspaceIdAndUserId(workspaceId, userId)
                .orElseThrow(() -> new MembershipNotFoundException("Membership not found"));
    }

    private void validateOwnerOrAdmin(MembershipModel membership) {
        if (membership.getRole() != MembershipModel.MembershipRole.OWNER &&
                membership.getRole() != MembershipModel.MembershipRole.ADMIN) {
            throw new WorkspaceAccessDeniedException("You do not have permission to perform this action");
        }
    }
}