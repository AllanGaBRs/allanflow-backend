package com.allan.task.manager.board.dto;

import java.util.UUID;

public record BoardMemberResponseDTO(
        UUID userId,
        String name,
        String email
) {
}
