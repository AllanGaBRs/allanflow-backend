package com.allan.task.manager.membership;

import com.allan.task.manager.membership.dto.MembershipCreateDTO;
import com.allan.task.manager.membership.dto.MembershipResponseDTO;
import com.allan.task.manager.membership.dto.MembershipUpdateRoleDTO;
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
@RequestMapping("/workspaces/{workspaceId}/members")
public class MembershipController {

    private final MembershipService membershipService;

    public MembershipController(MembershipService membershipService) {
        this.membershipService = membershipService;
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping
    public ResponseEntity<MembershipResponseDTO> addMember(
            @PathVariable UUID workspaceId,
            @RequestBody @Valid MembershipCreateDTO dto,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID requesterId = UUID.fromString(jwt.getClaimAsString("userId"));

        MembershipResponseDTO response = membershipService.addMember(
                workspaceId,
                dto,
                requesterId
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping
    public ResponseEntity<List<MembershipResponseDTO>> findMembers(
            @PathVariable UUID workspaceId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID requesterId = UUID.fromString(jwt.getClaimAsString("userId"));

        List<MembershipResponseDTO> response = membershipService.findMembers(
                workspaceId,
                requesterId
        );

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping("/{userId}")
    public ResponseEntity<MembershipResponseDTO> updateRole(
            @PathVariable UUID workspaceId,
            @PathVariable UUID userId,
            @RequestBody @Valid MembershipUpdateRoleDTO dto,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID requesterId = UUID.fromString(jwt.getClaimAsString("userId"));

        MembershipResponseDTO response = membershipService.updateRole(
                workspaceId,
                userId,
                dto,
                requesterId
        );

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable UUID workspaceId,
            @PathVariable UUID userId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID requesterId = UUID.fromString(jwt.getClaimAsString("userId"));

        membershipService.removeMember(
                workspaceId,
                userId,
                requesterId
        );

        return ResponseEntity.noContent().build();
    }
}