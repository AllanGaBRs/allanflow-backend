package com.allan.task.manager.task.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.UUID;

public record TaskMoveDTO(
        @NotNull
        UUID targetColumnId,

        @NotNull
        @PositiveOrZero
        Integer targetPosition
) {}