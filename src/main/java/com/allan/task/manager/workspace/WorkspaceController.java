package com.allan.task.manager.workspace;

import com.allan.task.manager.workspace.dto.WorkspaceCreateDTO;
import com.allan.task.manager.workspace.dto.WorkspaceResponseDTO;
import com.allan.task.manager.workspace.dto.WorkspaceUpdateDTO;
import com.allan.task.manager.workspaceinvitation.WorkspaceInvitationService;
import com.allan.task.manager.workspaceinvitation.dto.WorkspaceInvitationRequestDTO;
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
    private final WorkspaceInvitationService workspaceInvitationService;

    public WorkspaceController(
            WorkspaceService workspaceService,
            WorkspaceInvitationService workspaceInvitationService
    ) {
        this.workspaceService = workspaceService;
        this.workspaceInvitationService = workspaceInvitationService;
    }
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping
    public ResponseEntity<WorkspaceResponseDTO> create(
            @RequestBody @Valid WorkspaceCreateDTO dto,
            @AuthenticationPrincipal Jwt jwt
    ) {
        WorkspaceResponseDTO response = workspaceService.create(dto, jwt.getSubject());

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
                workspaceService.findMyWorkspaces(jwt.getSubject());

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/{workspaceId}")
    public ResponseEntity<WorkspaceResponseDTO> findById(
            @PathVariable UUID workspaceId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        WorkspaceResponseDTO response =
                workspaceService.findById(workspaceId, jwt.getSubject());

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
                workspaceService.update(workspaceId, dto, jwt.getSubject());

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping("/{workspaceId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID workspaceId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        workspaceService.delete(workspaceId, jwt.getSubject());

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/{workspaceId}/invitations")
    public ResponseEntity<Void> invite(
            @PathVariable UUID workspaceId,
            @RequestBody @Valid WorkspaceInvitationRequestDTO dto,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID requesterId = UUID.fromString(jwt.getClaimAsString("userId"));
        workspaceInvitationService.invite(
                workspaceId,
                requesterId,
                dto
        );

        return ResponseEntity.noContent().build();
    }
}