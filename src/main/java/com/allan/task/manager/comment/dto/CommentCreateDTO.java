package com.allan.task.manager.comment.dto;

import jakarta.validation.constraints.NotBlank;

public record CommentCreateDTO(
        @NotBlank
        String content
) {
}