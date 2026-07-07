package com.allan.task.manager.comment.dto;

import jakarta.validation.constraints.NotBlank;

public record CommentRequestDTO(

        @NotBlank
        String content

) {
}