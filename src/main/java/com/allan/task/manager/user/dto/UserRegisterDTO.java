package com.allan.task.manager.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRegisterDTO(
        @NotBlank @Size(min = 3, max = 100)
        String name,

        @NotBlank @Email
        String email,

        @NotBlank @Size(min = 6, max = 100)
        String password
) {}