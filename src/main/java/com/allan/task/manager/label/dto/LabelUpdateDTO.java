package com.allan.task.manager.label.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LabelUpdateDTO(

        @NotBlank(message = "Label name is required")
        @Size(min = 2, max = 150, message = "Label name must be between 2 and 150 characters")
        String name,

        @NotBlank(message = "Label color is required")
        String color
) {
}