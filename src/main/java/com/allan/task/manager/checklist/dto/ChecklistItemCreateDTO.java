package com.allan.task.manager.checklist.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChecklistItemCreateDTO(
        @NotBlank(message = "Checklist item content is required")
        @Size(min = 1, max = 255, message = "Checklist item content must be between 1 and 255 characters")
        String content,

        @Min(value = 0, message = "Checklist item position must be greater than or equal to 0")
        Integer position
) {
}
