package com.allan.task.manager.document.dto;

import com.allan.task.manager.document.DocumentModel;

import java.util.List;
import java.util.UUID;

public record DocumentTreeDTO(
        UUID id,
        String title,
        DocumentModel.Type type,
        UUID parentId,
        List<DocumentTreeDTO> children
) {
}
