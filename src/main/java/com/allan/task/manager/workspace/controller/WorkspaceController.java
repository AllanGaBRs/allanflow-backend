package com.allan.task.manager.workspace.controller;

import com.allan.task.manager.workspace.WorkspaceService;
import com.allan.task.manager.workspace.dto.WorkspaceCreateDTO;
import com.allan.task.manager.workspace.dto.WorkspaceResponseDTO;
import com.allan.task.manager.workspace.dto.WorkspaceUpdateDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/workspaces")
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    public WorkspaceController(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }

    @PostMapping
    public ResponseEntity<WorkspaceResponseDTO> create(
            @RequestBody @Valid WorkspaceCreateDTO dto,
            @AuthenticationPrincipal(expression = "username") String email
    ) {
        WorkspaceResponseDTO response = workspaceService.create(dto, email);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<List<WorkspaceResponseDTO>> findMyWorkspaces(
            @AuthenticationPrincipal(expression = "username") String email
    ) {
        List<WorkspaceResponseDTO> response =
                workspaceService.findMyWorkspaces(email);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{workspaceId}")
    public ResponseEntity<WorkspaceResponseDTO> findById(
            @PathVariable UUID workspaceId,
            @AuthenticationPrincipal(expression = "username") String email
    ) {
        WorkspaceResponseDTO response =
                workspaceService.findById(workspaceId, email);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{workspaceId}")
    public ResponseEntity<WorkspaceResponseDTO> update(
            @PathVariable UUID workspaceId,
            @RequestBody @Valid WorkspaceUpdateDTO dto,
            @AuthenticationPrincipal(expression = "username") String email
    ) {
        WorkspaceResponseDTO response =
                workspaceService.update(workspaceId, dto, email);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{workspaceId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID workspaceId,
            @AuthenticationPrincipal(expression = "username") String email
    ) {
        workspaceService.delete(workspaceId, email);

        return ResponseEntity.noContent().build();
    }
}