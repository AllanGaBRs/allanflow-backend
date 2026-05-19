package com.allan.task.manager.shared;

import com.allan.task.manager.membership.MembershipModel;
import com.allan.task.manager.membership.MembershipRepository;
import com.allan.task.manager.user.UserModel;
import com.allan.task.manager.user.UserRepository;
import com.allan.task.manager.user.exception.UserNotFoundException;
import com.allan.task.manager.workspace.WorkspaceModel;
import com.allan.task.manager.workspace.WorkspaceRepository;
import com.allan.task.manager.workspace.exception.WorkspaceAccessDeniedException;
import com.allan.task.manager.workspace.exception.WorkspaceNotFoundException;

import java.text.Normalizer;
import java.util.UUID;
import java.util.function.Supplier;

public final class Utils {

    private Utils() {
    }

    public static UserModel findActiveUserByEmail(UserRepository userRepository, String email) {
        return userRepository.findByEmailAndIsActiveTrue(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    public static UserModel findActiveUserById(UserRepository userRepository, UUID id) {
        return userRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    public static WorkspaceModel findActiveWorkspaceById(
            WorkspaceRepository workspaceRepository,
            UUID workspaceId
    ) {
        return workspaceRepository.findByIdAndIsActiveTrue(workspaceId)
                .orElseThrow(() -> new WorkspaceNotFoundException("Workspace not found"));
    }

    public static MembershipModel findMembership(
            MembershipRepository membershipRepository,
            UUID workspaceId,
            UUID userId,
            Supplier<? extends RuntimeException> exceptionSupplier
    ) {
        return membershipRepository.findByWorkspaceIdAndUserId(workspaceId, userId)
                .orElseThrow(exceptionSupplier);
    }

    public static void validateOwner(MembershipModel membership) {
        if (membership.getRole() != MembershipModel.MembershipRole.OWNER) {
            throw new WorkspaceAccessDeniedException("Only workspace owner can perform this action");
        }
    }

    public static void validateOwnerOrAdmin(MembershipModel membership) {
        if (membership.getRole() != MembershipModel.MembershipRole.OWNER &&
                membership.getRole() != MembershipModel.MembershipRole.ADMIN) {
            throw new WorkspaceAccessDeniedException("You do not have permission to perform this action");
        }
    }

    public static String generateUniqueSlug(WorkspaceRepository workspaceRepository, String name) {
        String baseSlug = slugify(name);
        String slug = baseSlug;
        int counter = 1;

        while (workspaceRepository.existsBySlugAndIsActiveTrue(slug)) {
            slug = baseSlug + "-" + counter;
            counter++;
        }

        return slug;
    }

    public static String slugify(String value) {
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
