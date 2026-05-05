package com.allan.task.manager.membership.dto;

import com.allan.task.manager.membership.MembershipModel;
import jakarta.validation.constraints.NotNull;

public record MembershipUpdateRoleDTO(
        @NotNull(message = "Role is required")
        MembershipModel.MembershipRole role
) {
}