package com.allan.task.manager.label.dto;

import java.util.UUID;

public record LabelResponseDTO(
        UUID id,
        String name,
        String color
) {
}
