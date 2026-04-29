package com.allan.task.manager.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserChangePasswordDTO(
        @NotBlank
        String currentPassword,

        @NotBlank
        @Size(min = 6, max = 100)
        String newPassword
) {
}