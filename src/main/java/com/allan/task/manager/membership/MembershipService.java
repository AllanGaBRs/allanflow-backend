package com.allan.task.manager.membership;

import com.allan.task.manager.membership.dto.MembershipCreateDTO;
import com.allan.task.manager.membership.dto.MembershipResponseDTO;
import com.allan.task.manager.membership.dto.MembershipUpdateRoleDTO;
import com.allan.task.manager.membership.exception.MembershipAlreadyExistsException;
import com.allan.task.manager.membership.exception.MembershipNotFoundException;
import com.allan.task.manager.membership.mapper.MembershipMapper;
import com.allan.task.manager.shared.Utils;
import com.allan.task.manager.user.UserModel;
import com.allan.task.manager.user.UserRepository;
import com.allan.task.manager.user.exception.UserNotFoundException;
import com.allan.task.manager.workspace.WorkspaceModel;
import com.allan.task.manager.workspace.WorkspaceRepository;
import com.allan.task.manager.workspace.exception.WorkspaceAccessDeniedException;
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
        MembershipModel requesterMembership = Utils.findMembership(
                membershipRepository,
                workspaceId,
                requesterId,
                () -> new MembershipNotFoundException("Membership not found")
        );
        Utils.validateOwnerOrAdmin(requesterMembership);

        WorkspaceModel workspace = Utils.findActiveWorkspaceById(workspaceRepository, workspaceId);

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
        Utils.findMembership(
                membershipRepository,
                workspaceId,
                requesterId,
                () -> new MembershipNotFoundException("Membership not found")
        );

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
        MembershipModel requesterMembership = Utils.findMembership(
                membershipRepository,
                workspaceId,
                requesterId,
                () -> new MembershipNotFoundException("Membership not found")
        );
        Utils.validateOwnerOrAdmin(requesterMembership);

        MembershipModel targetMembership = Utils.findMembership(
                membershipRepository,
                workspaceId,
                targetUserId,
                () -> new MembershipNotFoundException("Membership not found")
        );

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
        MembershipModel requesterMembership = Utils.findMembership(
                membershipRepository,
                workspaceId,
                requesterId,
                () -> new MembershipNotFoundException("Membership not found")
        );
        Utils.validateOwnerOrAdmin(requesterMembership);

        MembershipModel targetMembership = Utils.findMembership(
                membershipRepository,
                workspaceId,
                targetUserId,
                () -> new MembershipNotFoundException("Membership not found")
        );

        if (targetMembership.getRole() == MembershipModel.MembershipRole.OWNER) {
            throw new WorkspaceAccessDeniedException("Cannot remove workspace owner");
        }

        if (requesterId.equals(targetUserId)) {
            throw new WorkspaceAccessDeniedException("You cannot remove yourself from the workspace");
        }

        membershipRepository.delete(targetMembership);
    }
}
