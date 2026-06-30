package com.allan.task.manager.client.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record ClientResponseDTO(
        UUID id,
        String name,
        String email,
        String phone,
        String company
) {
}
