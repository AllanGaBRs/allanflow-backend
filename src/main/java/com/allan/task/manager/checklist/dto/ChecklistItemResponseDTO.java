package com.allan.task.manager.checklist.dto;

import java.util.UUID;

public record ChecklistItemResponseDTO(
        UUID id,
        String content,
        Boolean checked,
        Integer position
) {
}
