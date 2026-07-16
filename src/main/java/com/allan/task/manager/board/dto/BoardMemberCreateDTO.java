package com.allan.task.manager.board.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record BoardMemberCreateDTO(
        @NotNull
        UUID userId
) {
}
