package com.allan.task.manager.board.dto;

import jakarta.validation.constraints.Size;

public record BoardUpdateDTO(
        @Size(min = 2, max = 120, message = "Board name must be between 2 and 120 characters")
        String name,

        @Size(max = 255, message = "Board description must have at most 255 characters")
        String description
) {
}
