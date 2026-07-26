package com.allan.task.manager.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequestDTO(

        @NotBlank
        @Email
        String email

) {
}