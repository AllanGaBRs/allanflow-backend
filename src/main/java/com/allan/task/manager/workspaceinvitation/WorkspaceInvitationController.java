package com.allan.task.manager.workspaceinvitation;

import com.allan.task.manager.workspaceinvitation.dto.WorkspaceInvitationAcceptDTO;
import com.allan.task.manager.workspaceinvitation.dto.WorkspaceInvitationResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/invitations")
public class WorkspaceInvitationController {

    private final WorkspaceInvitationService workspaceInvitationService;

    public WorkspaceInvitationController(
            WorkspaceInvitationService workspaceInvitationService
    ) {
        this.workspaceInvitationService = workspaceInvitationService;
    }

    @GetMapping("/{invitationId}")
    public ResponseEntity<WorkspaceInvitationResponseDTO> findInvitation(
            @PathVariable UUID invitationId
    ) {
        WorkspaceInvitationResponseDTO response =
                workspaceInvitationService.findInvitation(invitationId);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/{invitationId}/accept")
    public ResponseEntity<Void> accept(
            @PathVariable UUID invitationId,
            @RequestBody @Valid WorkspaceInvitationAcceptDTO request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID authenticatedUserId = UUID.fromString(jwt.getClaimAsString("userId"));

        workspaceInvitationService.accept(
                invitationId,
                authenticatedUserId,
                request
        );

        return ResponseEntity.noContent().build();
    }
}
