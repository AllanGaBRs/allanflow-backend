package com.allan.task.manager.document;

import com.allan.task.manager.config.annotation.CurrentUserId;
import com.allan.task.manager.document.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/tree")
    public ResponseEntity<List<DocumentTreeDTO>> findTree(
            @PathVariable UUID workspaceId,
            @PathVariable UUID boardId,
            @CurrentUserId UUID requesterId
    ) {
        List<DocumentTreeDTO> response = documentService.findTree(
                workspaceId,
                boardId,
                requesterId
        );

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/{documentId}")
    public ResponseEntity<DocumentResponseDTO> findById(
            @PathVariable UUID workspaceId,
            @PathVariable UUID boardId,
            @PathVariable UUID documentId,
            @CurrentUserId UUID requesterId
    ) {
        DocumentResponseDTO response = documentService.findById(
                workspaceId,
                boardId,
                documentId,
                requesterId
        );

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PutMapping("/{documentId}")
    public ResponseEntity<DocumentResponseDTO> update(
            @PathVariable UUID workspaceId,
            @PathVariable UUID boardId,
            @PathVariable UUID documentId,
            @RequestBody @Valid DocumentUpdateDTO dto,
            @CurrentUserId UUID requesterId
    ) {
        DocumentResponseDTO response = documentService.update(
                workspaceId,
                boardId,
                documentId,
                dto,
                requesterId
        );

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @DeleteMapping("/{documentId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID workspaceId,
            @PathVariable UUID boardId,
            @PathVariable UUID documentId,
            @CurrentUserId UUID requesterId
    ) {
        documentService.delete(
                workspaceId,
                boardId,
                documentId,
                requesterId
        );

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PatchMapping("/{documentId}/move")
    public ResponseEntity<DocumentResponseDTO> move(
            @PathVariable UUID workspaceId,
            @PathVariable UUID boardId,
            @PathVariable UUID documentId,
            @RequestBody DocumentMoveDTO dto,
            @CurrentUserId UUID requesterId
    ){
        DocumentResponseDTO response = documentService.move(
                workspaceId,
                boardId,
                documentId,
                dto,
                requesterId
        );

        return ResponseEntity.ok(response);
    }
}