package com.allan.task.manager.workspace;

import com.allan.task.manager.membership.MembershipModel;
import com.allan.task.manager.membership.MembershipRepository;
import com.allan.task.manager.workspace.exception.WorkspaceAccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class WorkspacePermissionService {

    private final MembershipRepository membershipRepository;

    public WorkspacePermissionService(MembershipRepository membershipRepository) {
        this.membershipRepository = membershipRepository;
    }

    public MembershipModel requireMember(UUID workspaceId, UUID userId) {
        return membershipRepository.findByWorkspaceIdAndUserId(workspaceId, userId)
                .orElseThrow(() -> new WorkspaceAccessDeniedException("You do not have access to this workspace"));
    }

    public MembershipModel requireOwner(UUID workspaceId, UUID userId) {
        MembershipModel membership = requireMember(workspaceId, userId);

        if (membership.getRole() != MembershipModel.MembershipRole.OWNER) {
            throw new WorkspaceAccessDeniedException("Only workspace owner can perform this action");
        }

        return membership;
    }

    public MembershipModel requireOwnerOrAdmin(UUID workspaceId, UUID userId) {
        MembershipModel membership = requireMember(workspaceId, userId);

        if (membership.getRole() != MembershipModel.MembershipRole.OWNER &&
                membership.getRole() != MembershipModel.MembershipRole.ADMIN) {
            throw new WorkspaceAccessDeniedException("You do not have permission to perform this action");
        }

        return membership;
    }
}
