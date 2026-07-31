package com.allan.task.manager.task;

import com.allan.task.manager.config.annotation.CurrentUserId;
import com.allan.task.manager.task.dto.TaskCreateDTO;
import com.allan.task.manager.task.dto.TaskMoveDTO;
import com.allan.task.manager.task.dto.TaskResponseDTO;
import com.allan.task.manager.task.dto.TaskUpdateDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.config.Task;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/workspaces/{workspaceId}/boards/{boardId}/columns/{columnId}/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping
    public ResponseEntity<TaskResponseDTO> create(
            @PathVariable UUID workspaceId,
            @PathVariable UUID boardId,
            @PathVariable UUID columnId,
            @RequestBody @Valid TaskCreateDTO dto,
            @CurrentUserId UUID requesterId
    ) {
        TaskResponseDTO response = taskService.create(
                dto,
                workspaceId,
                boardId,
                columnId,
                requesterId
        );
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponseDTO> update(
            @PathVariable UUID workspaceId,
            @PathVariable UUID boardId,
            @PathVariable UUID columnId,
            @PathVariable UUID taskId,
            @RequestBody @Valid TaskUpdateDTO dto,
            @CurrentUserId UUID requesterId
    ) {
        TaskResponseDTO response = taskService.update(
                workspaceId,
                boardId,
                columnId,
                taskId,
                dto,
                requesterId
        );
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponseDTO> findById(
            @PathVariable UUID workspaceId,
            @PathVariable UUID boardId,
            @PathVariable UUID columnId,
            @PathVariable UUID taskId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID requesterId = UUID.fromString(jwt.getClaimAsString("userId"));

        TaskResponseDTO response = taskService.findById(
                workspaceId,
                boardId,
                columnId,
                taskId,
                requesterId
        );

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID workspaceId,
            @PathVariable UUID boardId,
            @PathVariable UUID columnId,
            @PathVariable UUID taskId,
            @CurrentUserId UUID requesterId
    ) {
        taskService.delete(
                workspaceId,
                boardId,
                columnId,
                taskId,
                requesterId
        );
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping
    public ResponseEntity<List<TaskResponseDTO>> findAllByColumn(
            @PathVariable UUID workspaceId,
            @PathVariable UUID boardId,
            @PathVariable UUID columnId,
            @CurrentUserId UUID requesterId
    ) {
        List<TaskResponseDTO> response = taskService.findAllByColumn(
                workspaceId,
                boardId,
                columnId,
                requesterId
        );
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PatchMapping("/{taskId}/move")
    public ResponseEntity<TaskResponseDTO> moveTask(
            @PathVariable UUID workspaceId,
            @PathVariable UUID boardId,
            @PathVariable UUID columnId,
            @PathVariable UUID taskId,
            @RequestBody @Valid TaskMoveDTO dto,
            @CurrentUserId UUID requesterId
    ) {
        TaskResponseDTO response = taskService.moveTask(
                workspaceId,
                boardId,
                columnId,
                taskId,
                dto,
                requesterId
        );
        return ResponseEntity.ok(response);
    }
}