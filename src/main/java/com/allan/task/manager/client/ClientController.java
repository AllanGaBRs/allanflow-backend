package com.allan.task.manager.client;

import com.allan.task.manager.client.dto.ClientCreateDTO;
import com.allan.task.manager.client.dto.ClientResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/workspaces/{workspaceId}/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping
    public ResponseEntity<ClientResponseDTO> create(
            @PathVariable UUID workspaceId,
            @RequestBody @Valid ClientCreateDTO dto,
            @AuthenticationPrincipal Jwt jwt
    ){
        UUID requesterId = UUID.fromString(jwt.getClaimAsString("userId"));
        ClientResponseDTO response = clientService.create(workspaceId, dto, requesterId );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
