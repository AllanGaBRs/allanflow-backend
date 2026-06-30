package com.allan.task.manager.client.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClientCreateDTO(

        @NotBlank(message = "Client name is required")
        @Size(min = 2, max = 150, message = "Client name must be between 2 and 150 characters")
        String name,

        @Email
        String email,

        String phone,
        String company,

        boolean forceCreate
) {
}
