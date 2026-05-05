package com.allan.task.manager.membership.dto;

import com.allan.task.manager.membership.MembershipModel;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MembershipCreateDTO(
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email")
        String email,

        @NotNull(message = "Role is required")
        MembershipModel.MembershipRole role
) {
}