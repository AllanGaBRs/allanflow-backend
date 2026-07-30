package com.allan.task.manager.ai.dto;

import java.util.UUID;

public record N8nChatRequestDTO(
        String message,
        UUID userId
) {
}