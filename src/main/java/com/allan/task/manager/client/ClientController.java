package com.allan.task.manager.client;

import com.allan.task.manager.client.dto.ClientCreateDTO;
import com.allan.task.manager.client.dto.ClientResponseDTO;
import com.allan.task.manager.client.dto.ClientUpdateDTO;
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
@RequestMapping("/workspaces/{workspaceId}/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping
    public ResponseEntity<ClientResponseDTO> create(
            @PathVariable UUID workspaceId,
            @RequestBody @Valid ClientCreateDTO dto,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID requesterId = UUID.fromString(jwt.getClaimAsString("userId"));

        ClientResponseDTO response =
                clientService.create(workspaceId, dto, requesterId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PutMapping("/{clientId}")
    public ResponseEntity<ClientResponseDTO> update(
            @PathVariable UUID workspaceId,
            @PathVariable UUID clientId,
            @RequestBody @Valid ClientUpdateDTO dto,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID requesterId = UUID.fromString(jwt.getClaimAsString("userId"));

        ClientResponseDTO response =
                clientService.update(workspaceId, clientId, dto, requesterId);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/{clientId}")
    public ResponseEntity<ClientResponseDTO> findById(
            @PathVariable UUID workspaceId,
            @PathVariable UUID clientId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID requesterId = UUID.fromString(jwt.getClaimAsString("userId"));

        ClientResponseDTO response =
                clientService.findById(workspaceId, clientId, requesterId);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping
    public ResponseEntity<List<ClientResponseDTO>> findAll(
            @PathVariable UUID workspaceId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID requesterId = UUID.fromString(jwt.getClaimAsString("userId"));

        List<ClientResponseDTO> response =
                clientService.findAll(workspaceId, requesterId);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @DeleteMapping("/{clientId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID workspaceId,
            @PathVariable UUID clientId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID requesterId = UUID.fromString(jwt.getClaimAsString("userId"));

        clientService.delete(workspaceId, clientId, requesterId);

        return ResponseEntity.noContent().build();
    }
}