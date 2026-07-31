package com.allan.task.manager.workspace;

import com.allan.task.manager.config.annotation.CurrentUserId;
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

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping
    public ResponseEntity<WorkspaceResponseDTO> create(
            @RequestBody @Valid WorkspaceCreateDTO dto,
            @CurrentUserId UUID requesterId
    ) {
        WorkspaceResponseDTO response = workspaceService.create(dto, requesterId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/me")
    public ResponseEntity<List<WorkspaceResponseDTO>> findMyWorkspaces(
            @AuthenticationPrincipal Jwt jwt,
            @CurrentUserId UUID requesterId
    ) {
        List<WorkspaceResponseDTO> response =
                workspaceService.findMyWorkspaces(requesterId);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/{workspaceId}")
    public ResponseEntity<WorkspaceResponseDTO> findById(
            @PathVariable UUID workspaceId,
            @AuthenticationPrincipal Jwt jwt,
            @CurrentUserId UUID requesterId
    ) {
        WorkspaceResponseDTO response =
                workspaceService.findById(workspaceId, requesterId);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PutMapping("/{workspaceId}")
    public ResponseEntity<WorkspaceResponseDTO> update(
            @PathVariable UUID workspaceId,
            @RequestBody @Valid WorkspaceUpdateDTO dto,
            @AuthenticationPrincipal Jwt jwt,
            @CurrentUserId UUID requesterId
    ) {
        WorkspaceResponseDTO response =
                workspaceService.update(workspaceId, dto, requesterId);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @DeleteMapping("/{workspaceId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID workspaceId,
            @AuthenticationPrincipal Jwt jwt,
            @CurrentUserId UUID requesterId
    ) {
        workspaceService.delete(workspaceId, requesterId);

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping("/{workspaceId}/invitations")
    public ResponseEntity<Void> invite(
            @PathVariable UUID workspaceId,
            @RequestBody @Valid WorkspaceInvitationRequestDTO dto,
            @CurrentUserId UUID requesterId
    ) {
        workspaceInvitationService.invite(
                workspaceId,
                requesterId,
                dto
        );

        return ResponseEntity.noContent().build();
    }
}