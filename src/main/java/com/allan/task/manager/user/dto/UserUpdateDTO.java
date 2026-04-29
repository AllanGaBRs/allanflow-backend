package com.allan.task.manager.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserUpdateDTO(
        @Size(min = 3, max = 100)
        String name,

        @Email(message = "Invalid email format")
        String email
) {
}