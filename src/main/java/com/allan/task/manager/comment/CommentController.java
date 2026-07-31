package com.allan.task.manager.comment;

import com.allan.task.manager.comment.dto.CommentCreateDTO;
import com.allan.task.manager.comment.dto.CommentResponseDTO;
import com.allan.task.manager.comment.dto.CommentUpdateDTO;
import com.allan.task.manager.config.annotation.CurrentUserId;
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
@RequestMapping("/workspaces/{workspaceId}/boards/{boardId}/columns/{columnId}/tasks/{taskId}/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping
    public ResponseEntity<CommentResponseDTO> create(
            @PathVariable UUID workspaceId,
            @PathVariable UUID boardId,
            @PathVariable UUID columnId,
            @PathVariable UUID taskId,
            @RequestBody @Valid CommentCreateDTO dto,
            @CurrentUserId UUID requesterId
    ) {
        CommentResponseDTO response = commentService.create(
                workspaceId,
                boardId,
                columnId,
                taskId,
                dto,
                requesterId
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponseDTO> update(
            @PathVariable UUID workspaceId,
            @PathVariable UUID boardId,
            @PathVariable UUID columnId,
            @PathVariable UUID taskId,
            @PathVariable UUID commentId,
            @RequestBody @Valid CommentUpdateDTO dto,
            @CurrentUserId UUID requesterId
    ) {
        CommentResponseDTO response = commentService.update(
                workspaceId,
                boardId,
                columnId,
                taskId,
                commentId,
                dto,
                requesterId
        );
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/{commentId}")
    public ResponseEntity<CommentResponseDTO> findById(
            @PathVariable UUID workspaceId,
            @PathVariable UUID boardId,
            @PathVariable UUID columnId,
            @PathVariable UUID taskId,
            @PathVariable UUID commentId,
            @CurrentUserId UUID requesterId
    ) {
        CommentResponseDTO response = commentService.findById(
                workspaceId,
                boardId,
                columnId,
                taskId,
                commentId,
                requesterId
        );
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping
    public ResponseEntity<List<CommentResponseDTO>> findAllByTask(
            @PathVariable UUID workspaceId,
            @PathVariable UUID boardId,
            @PathVariable UUID columnId,
            @PathVariable UUID taskId,
            @CurrentUserId UUID requesterId
    ) {
        List<CommentResponseDTO> response = commentService.findAllByTask(
                workspaceId,
                boardId,
                columnId,
                taskId,
                requesterId
        );
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID workspaceId,
            @PathVariable UUID boardId,
            @PathVariable UUID columnId,
            @PathVariable UUID taskId,
            @PathVariable UUID commentId,
            @CurrentUserId UUID requesterId
    ) {
        commentService.delete(
                workspaceId,
                boardId,
                columnId,
                taskId,
                commentId,
                requesterId
        );
        return ResponseEntity.noContent().build();
    }
}