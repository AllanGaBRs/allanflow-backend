package com.allan.task.manager.comment.dto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public record CommentResponseDTO(
        UUID id,
        String content,
        UUID authorId,
        String authorName,
        UUID taskId,
        LocalDateTime createdAt
) {
}