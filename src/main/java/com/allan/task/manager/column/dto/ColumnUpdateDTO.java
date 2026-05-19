package com.allan.task.manager.column.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record ColumnUpdateDTO(
        @Size(min = 2, max = 100, message = "Column name must be between 2 and 100 characters")
        String name,

        @Min(value = 0, message = "Column position must be greater than or equal to 0")
        Integer position
) {
}
