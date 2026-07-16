package com.allan.task.manager.membership;

import com.allan.task.manager.membership.exception.MembershipNotFoundException;
import com.allan.task.manager.user.UserModel;
import com.allan.task.manager.user.exception.UserNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class MembershipLookupService {

    private final MembershipRepository membershipRepository;

    public MembershipLookupService(MembershipRepository membershipRepository) {
        this.membershipRepository = membershipRepository;
    }

    public MembershipModel findByWorkspaceAndUser(UUID workspaceId, UUID userId) {
        return membershipRepository.findByWorkspaceIdAndUserId(workspaceId, userId)
                .orElseThrow(() -> new MembershipNotFoundException("Membership not found"));
    }

    public List<UserModel> findActiveUsersInWorkspaceByIds(UUID workspaceId, Set<UUID> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return List.of();
        }

        List<UserModel> users = membershipRepository.findActiveUsersInActiveWorkspaceByIds(workspaceId, userIds);

        if (users.size() != userIds.size()) {
            throw new UserNotFoundException("One or more users were not found in this workspace");
        }

        return users;
    }
}
