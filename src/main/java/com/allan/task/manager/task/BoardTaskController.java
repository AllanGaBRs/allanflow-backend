package com.allan.task.manager.task;

import com.allan.task.manager.task.dto.TaskResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/workspaces/{workspaceId}/boards/{boardId}/tasks")
public class BoardTaskController {

    private final TaskService taskService;

    public BoardTaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping
    public ResponseEntity<List<TaskResponseDTO>> findAllByBoard(
            @PathVariable UUID workspaceId,
            @PathVariable UUID boardId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID requesterId = UUID.fromString(
                jwt.getClaimAsString("userId")
        );

        List<TaskResponseDTO> response = taskService.findAllByBoard(
                workspaceId,
                boardId,
                requesterId
        );

        return ResponseEntity.ok(response);
    }
}