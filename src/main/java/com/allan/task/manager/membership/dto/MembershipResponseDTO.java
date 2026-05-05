package com.allan.task.manager.membership.dto;

import com.allan.task.manager.membership.MembershipModel;

import java.util.UUID;

public record MembershipResponseDTO(
        UUID membershipId,
        UUID userId,
        String name,
        String email,
        MembershipModel.MembershipRole role
) {
}