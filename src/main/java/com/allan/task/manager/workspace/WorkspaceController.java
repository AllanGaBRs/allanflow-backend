package com.allan.task.manager.workspace;

import com.allan.task.manager.workspace.dto.WorkspaceCreateDTO;
import com.allan.task.manager.workspace.dto.WorkspaceResponseDTO;
import com.allan.task.manager.workspace.dto.WorkspaceUpdateDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/workspaces")
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    public WorkspaceController(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping
    public ResponseEntity<WorkspaceResponseDTO> create(
            @RequestBody @Valid WorkspaceCreateDTO dto,
            @AuthenticationPrincipal Jwt jwt
    ) {
        WorkspaceResponseDTO response = workspaceService.create(dto, jwt.getClaimAsString("username"));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/me")
    public ResponseEntity<List<WorkspaceResponseDTO>> findMyWorkspaces(
            @AuthenticationPrincipal Jwt jwt
    ) {
        List<WorkspaceResponseDTO> response =
                workspaceService.findMyWorkspaces(jwt.getClaimAsString("username"));

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/{workspaceId}")
    public ResponseEntity<WorkspaceResponseDTO> findById(
            @PathVariable UUID workspaceId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        WorkspaceResponseDTO response =
                workspaceService.findById(workspaceId, jwt.getClaimAsString("username"));

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping("/{workspaceId}")
    public ResponseEntity<WorkspaceResponseDTO> update(
            @PathVariable UUID workspaceId,
            @RequestBody @Valid WorkspaceUpdateDTO dto,
            @AuthenticationPrincipal Jwt jwt
    ) {
        WorkspaceResponseDTO response =
                workspaceService.update(workspaceId, dto, jwt.getClaimAsString("username"));

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping("/{workspaceId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID workspaceId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        workspaceService.delete(workspaceId, jwt.getClaimAsString("username"));

        return ResponseEntity.noContent().build();
    }
}