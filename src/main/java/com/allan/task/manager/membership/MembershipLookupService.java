package com.allan.task.manager.membership;

import com.allan.task.manager.membership.exception.MembershipNotFoundException;
import org.springframework.stereotype.Service;

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
}
