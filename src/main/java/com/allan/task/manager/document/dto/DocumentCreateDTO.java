package com.allan.task.manager.document.dto;

import com.allan.task.manager.document.DocumentModel;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record DocumentCreateDTO(
        @NotBlank(message = "Title is required")
        @Size(min = 2, max = 255, message = "Title must be between 2 and 255 characters")
        String title,

        @NotNull(message = "Type is required")
        DocumentModel.Type type,

        UUID parentId,

        JsonNode content
) {
}
