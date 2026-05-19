package com.allan.task.manager.column.dto;

import java.util.UUID;

public record ColumnResponseDTO(
        UUID id,
        String name,
        Integer position
) {
}
