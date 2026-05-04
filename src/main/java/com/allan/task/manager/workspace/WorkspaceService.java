package com.allan.task.manager.workspace;

import com.allan.task.manager.membership.MembershipModel;
import com.allan.task.manager.membership.MembershipRepository;
import com.allan.task.manager.user.UserModel;
import com.allan.task.manager.user.UserRepository;
import com.allan.task.manager.user.exception.UserNotFoundException;
import com.allan.task.manager.workspace.dto.WorkspaceCreateDTO;
import com.allan.task.manager.workspace.dto.WorkspaceResponseDTO;
import com.allan.task.manager.workspace.dto.WorkspaceUpdateDTO;
import com.allan.task.manager.workspace.exception.WorkspaceAccessDeniedException;
import com.allan.task.manager.workspace.exception.WorkspaceNotFoundException;
import com.allan.task.manager.workspace.mapper.WorkspaceMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
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
        UserModel owner = findActiveUserByEmail(authenticatedUserEmail);

        WorkspaceModel workspace = new WorkspaceModel();
        workspace.setName(dto.name());
        workspace.setSlug(generateUniqueSlug(dto.name()));
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
        UserModel user = findActiveUserByEmail(authenticatedUserEmail);

        List<MembershipModel> memberships =
                membershipRepository.findActiveMembershipsByUserId(user.getId());

        return WorkspaceMapper.toResponseList(memberships);
    }

    @Transactional(readOnly = true)
    public WorkspaceResponseDTO findById(UUID workspaceId, String authenticatedUserEmail) {
        UserModel user = findActiveUserByEmail(authenticatedUserEmail);

        MembershipModel membership = findMembership(workspaceId, user.getId());

        WorkspaceModel workspace = findActiveWorkspaceById(workspaceId);

        return WorkspaceMapper.toResponse(workspace, membership);
    }

    @Transactional
    public WorkspaceResponseDTO update(
            UUID workspaceId,
            WorkspaceUpdateDTO dto,
            String authenticatedUserEmail
    ) {
        UserModel user = findActiveUserByEmail(authenticatedUserEmail);

        MembershipModel membership = findMembership(workspaceId, user.getId());

        validateOwnerOrAdmin(membership);

        WorkspaceModel workspace = findActiveWorkspaceById(workspaceId);

        if (dto.name() != null && !dto.name().isBlank()) {
            workspace.setName(dto.name());
            workspace.setSlug(generateUniqueSlug(dto.name()));
        }

        WorkspaceModel updatedWorkspace = workspaceRepository.save(workspace);

        return WorkspaceMapper.toResponse(updatedWorkspace, membership);
    }

    @Transactional
    public void delete(UUID workspaceId, String authenticatedUserEmail) {
        UserModel user = findActiveUserByEmail(authenticatedUserEmail);

        MembershipModel membership = findMembership(workspaceId, user.getId());

        validateOwner(membership);

        WorkspaceModel workspace = findActiveWorkspaceById(workspaceId);

        workspace.setActive(false);

        workspaceRepository.save(workspace);
    }

    private UserModel findActiveUserByEmail(String email) {
        return userRepository.findByEmailAndIsActiveTrue(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    private WorkspaceModel findActiveWorkspaceById(UUID workspaceId) {
        return workspaceRepository.findByIdAndIsActiveTrue(workspaceId)
                .orElseThrow(() -> new WorkspaceNotFoundException("Workspace not found"));
    }

    private MembershipModel findMembership(UUID workspaceId, UUID userId) {
        return membershipRepository.findByWorkspaceIdAndUserId(workspaceId, userId)
                .orElseThrow(() -> new WorkspaceAccessDeniedException("You do not have access to this workspace"));
    }

    private void validateOwner(MembershipModel membership) {
        if (membership.getRole() != MembershipModel.MembershipRole.OWNER) {
            throw new WorkspaceAccessDeniedException("Only workspace owner can perform this action");
        }
    }

    private void validateOwnerOrAdmin(MembershipModel membership) {
        if (membership.getRole() != MembershipModel.MembershipRole.OWNER &&
                membership.getRole() != MembershipModel.MembershipRole.ADMIN) {
            throw new WorkspaceAccessDeniedException("You do not have permission to perform this action");
        }
    }

    private String generateUniqueSlug(String name) {
        String baseSlug = slugify(name);
        String slug = baseSlug;
        int counter = 1;

        while (workspaceRepository.existsBySlugAndIsActiveTrue(slug)) {
            slug = baseSlug + "-" + counter;
            counter++;
        }

        return slug;
    }

    private String slugify(String value) {
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        String slug = normalized
                .toLowerCase()
                .trim()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-");

        if (slug.isBlank()) {
            return "workspace";
        }

        return slug;
    }
}