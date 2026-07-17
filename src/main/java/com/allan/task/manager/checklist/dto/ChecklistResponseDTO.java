package com.allan.task.manager.checklist.dto;

import java.util.List;
import java.util.UUID;

public record ChecklistResponseDTO(
        UUID id,
        String title,
        UUID taskId,
        List<ChecklistItemResponseDTO> items
) {
}
