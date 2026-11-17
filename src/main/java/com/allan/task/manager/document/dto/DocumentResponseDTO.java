package com.allan.task.manager.document.dto;

import com.allan.task.manager.document.DocumentModel;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.UUID;

public record DocumentResponseDTO(
        UUID id,
        String title,
        DocumentModel.Type type,
        UUID parentId,
        JsonNode content
) {
}
