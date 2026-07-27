package com.allan.task.manager.auth.dto;

import java.util.UUID;

public record LoginResponseDTO(
        UUID userId,
        String name,
        String email
) {
}