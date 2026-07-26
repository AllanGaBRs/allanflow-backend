package com.allan.task.manager.passwordreset.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequestDTO(

        @NotBlank
        @Email
        String email,

        @NotBlank
        @Size(min = 7, max = 7)
        String code,

        @NotBlank
        @Size(min = 6)
        String newPassword

) {
}