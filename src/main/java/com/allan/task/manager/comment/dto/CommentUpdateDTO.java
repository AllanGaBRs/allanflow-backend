package com.allan.task.manager.comment.dto;

import jakarta.validation.constraints.NotBlank;

public record CommentUpdateDTO(
        @NotBlank
        String content
) {
}