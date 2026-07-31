package com.allan.task.manager.label;

import com.allan.task.manager.config.annotation.CurrentUserId;
import com.allan.task.manager.label.dto.LabelCreateDTO;
import com.allan.task.manager.label.dto.LabelResponseDTO;
import com.allan.task.manager.label.dto.LabelUpdateDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/workspaces/{workspaceId}/boards/{boardId}/labels")
public class LabelController {

    private final LabelService labelService;

    public LabelController(LabelService labelService) {
        this.labelService = labelService;
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping
    public ResponseEntity<LabelResponseDTO> create(
            @PathVariable UUID workspaceId,
            @PathVariable UUID boardId,
            @RequestBody @Valid LabelCreateDTO dto,
            @CurrentUserId UUID requesterId
    ) {
        LabelResponseDTO response =
                labelService.create(workspaceId, boardId, dto, requesterId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PutMapping("/{labelId}")
    public ResponseEntity<LabelResponseDTO> update(
            @PathVariable UUID workspaceId,
            @PathVariable UUID boardId,
            @PathVariable UUID labelId,
            @RequestBody @Valid LabelUpdateDTO dto,
            @CurrentUserId UUID requesterId
    ) {
        LabelResponseDTO response =
                labelService.update(workspaceId, boardId, labelId, dto, requesterId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/{labelId}")
    public ResponseEntity<LabelResponseDTO> findById(
            @PathVariable UUID workspaceId,
            @PathVariable UUID boardId,
            @PathVariable UUID labelId,
            @CurrentUserId UUID requesterId
    ) {
        LabelResponseDTO response =
                labelService.findById(workspaceId, boardId, labelId, requesterId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping
    public ResponseEntity<List<LabelResponseDTO>> findAll(
            @PathVariable UUID workspaceId,
            @PathVariable UUID boardId,
            @CurrentUserId UUID requesterId
    ) {
        List<LabelResponseDTO> response =
                labelService.findAll(workspaceId, boardId, requesterId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @DeleteMapping("/{labelId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID workspaceId,
            @PathVariable UUID boardId,
            @PathVariable UUID labelId,
            @CurrentUserId UUID requesterId
    ) {
        labelService.delete(workspaceId, boardId, labelId, requesterId);
        return ResponseEntity.noContent().build();
    }
}