package com.allan.task.manager.label;

import com.allan.task.manager.label.dto.LabelCreateDTO;
import com.allan.task.manager.label.dto.LabelResponseDTO;
import jakarta.validation.Valid;
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

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping
    public ResponseEntity<LabelResponseDTO> create(
            @PathVariable UUID workspaceId,
            @PathVariable UUID boardId,
            @RequestBody @Valid LabelCreateDTO dto,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID requesterId = UUID.fromString(jwt.getClaimAsString("userId"));
        LabelResponseDTO response =
                labelService.create(workspaceId, boardId, dto, requesterId);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/{labelId}")
    public ResponseEntity<LabelResponseDTO> findById(
            @PathVariable UUID workspaceId,
            @PathVariable UUID boardId,
            @PathVariable UUID labelId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID requesterId = UUID.fromString(jwt.getClaimAsString("userId"));
        LabelResponseDTO response =
                labelService.findById(workspaceId, boardId, labelId, requesterId);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping
    public ResponseEntity<List<LabelResponseDTO>> findAll(
            @PathVariable UUID workspaceId,
            @PathVariable UUID boardId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID requesterId = UUID.fromString(jwt.getClaimAsString("userId"));
        List<LabelResponseDTO> response =
                labelService.findAll(workspaceId, boardId, requesterId);

        return ResponseEntity.ok(response);
    }
}