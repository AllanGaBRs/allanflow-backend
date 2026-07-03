package com.allan.task.manager.task.dto;

import java.util.UUID;

public record TaskResponseDTO(
        UUID id,
        String name,
        String description
) {
}
