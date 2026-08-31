package com.allan.task.manager.document;

import com.allan.task.manager.config.annotation.CurrentUserId;
import com.allan.task.manager.document.dto.DocumentCreateDTO;
import com.allan.task.manager.document.dto.DocumentResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/workspaces/{workspaceId}/boards/{boardId}/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping
    public ResponseEntity<DocumentResponseDTO> create(
            @PathVariable UUID workspaceId,
            @PathVariable UUID boardId,
            @RequestBody @Valid DocumentCreateDTO dto,
            @CurrentUserId UUID requesterId
    ) {
        DocumentResponseDTO response = documentService.create(
                workspaceId,
                boardId,
                dto,
                requesterId
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}