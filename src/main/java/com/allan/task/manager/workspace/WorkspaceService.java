package com.allan.task.manager.workspace;

import com.allan.task.manager.membership.MembershipModel;
import com.allan.task.manager.membership.MembershipRepository;
import com.allan.task.manager.shared.Utils;
import com.allan.task.manager.user.UserModel;
import com.allan.task.manager.user.UserRepository;
import com.allan.task.manager.workspace.dto.WorkspaceCreateDTO;
import com.allan.task.manager.workspace.dto.WorkspaceResponseDTO;
import com.allan.task.manager.workspace.dto.WorkspaceUpdateDTO;
import com.allan.task.manager.workspace.exception.WorkspaceAccessDeniedException;
import com.allan.task.manager.workspace.mapper.WorkspaceMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final MembershipRepository membershipRepository;
    private final UserRepository userRepository;

    public WorkspaceService(
            WorkspaceRepository workspaceRepository,
            MembershipRepository membershipRepository,
            UserRepository userRepository
    ) {
        this.workspaceRepository = workspaceRepository;
        this.membershipRepository = membershipRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public WorkspaceResponseDTO create(WorkspaceCreateDTO dto, String authenticatedUserEmail) {
        UserModel owner = Utils.findActiveUserByEmail(userRepository, authenticatedUserEmail);

        WorkspaceModel workspace = new WorkspaceModel();
        workspace.setName(dto.name());
        workspace.setSlug(Utils.generateUniqueSlug(workspaceRepository, dto.name()));
        workspace.setOwner(owner);
        workspace.setActive(true);

        WorkspaceModel savedWorkspace = workspaceRepository.save(workspace);

        MembershipModel membership = new MembershipModel();
        membership.setUser(owner);
        membership.setWorkspace(savedWorkspace);
        membership.setRole(MembershipModel.MembershipRole.OWNER);

        MembershipModel savedMembership = membershipRepository.save(membership);

        return WorkspaceMapper.toResponse(savedWorkspace, savedMembership);
    }

    @Transactional(readOnly = true)
    public List<WorkspaceResponseDTO> findMyWorkspaces(String authenticatedUserEmail) {
        UserModel user = Utils.findActiveUserByEmail(userRepository, authenticatedUserEmail);

        List<MembershipModel> memberships =
                membershipRepository.findActiveMembershipsByUserId(user.getId());

        return WorkspaceMapper.toResponseList(memberships);
    }

    @Transactional(readOnly = true)
    public WorkspaceResponseDTO findById(UUID workspaceId, String authenticatedUserEmail) {
        UserModel user = Utils.findActiveUserByEmail(userRepository, authenticatedUserEmail);

        MembershipModel membership = Utils.findMembership(
                membershipRepository,
                workspaceId,
                user.getId(),
                () -> new WorkspaceAccessDeniedException("You do not have access to this workspace")
        );

        WorkspaceModel workspace = Utils.findActiveWorkspaceById(workspaceRepository, workspaceId);

        return WorkspaceMapper.toResponse(workspace, membership);
    }

    @Transactional
    public WorkspaceResponseDTO update(
            UUID workspaceId,
            WorkspaceUpdateDTO dto,
            String authenticatedUserEmail
    ) {
        UserModel user = Utils.findActiveUserByEmail(userRepository, authenticatedUserEmail);

        MembershipModel membership = Utils.findMembership(
                membershipRepository,
                workspaceId,
                user.getId(),
                () -> new WorkspaceAccessDeniedException("You do not have access to this workspace")
        );

        Utils.validateOwnerOrAdmin(membership);

        WorkspaceModel workspace = Utils.findActiveWorkspaceById(workspaceRepository, workspaceId);

        if (dto.name() != null && !dto.name().isBlank()) {
            workspace.setName(dto.name());
            workspace.setSlug(Utils.generateUniqueSlug(workspaceRepository, dto.name()));
        }

        WorkspaceModel updatedWorkspace = workspaceRepository.save(workspace);

        return WorkspaceMapper.toResponse(updatedWorkspace, membership);
    }

    @Transactional
    public void delete(UUID workspaceId, String authenticatedUserEmail) {
        UserModel user = Utils.findActiveUserByEmail(userRepository, authenticatedUserEmail);

        MembershipModel membership = Utils.findMembership(
                membershipRepository,
                workspaceId,
                user.getId(),
                () -> new WorkspaceAccessDeniedException("You do not have access to this workspace")
        );

        Utils.validateOwner(membership);

        WorkspaceModel workspace = Utils.findActiveWorkspaceById(workspaceRepository, workspaceId);

        workspace.setActive(false);

        workspaceRepository.save(workspace);
    }
}