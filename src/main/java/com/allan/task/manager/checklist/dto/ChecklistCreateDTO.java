package com.allan.task.manager.checklist.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChecklistCreateDTO(
        @NotBlank(message = "Checklist title is required")
        @Size(min = 2, max = 120, message = "Checklist title must be between 2 and 120 characters")
        String title
) {
}
