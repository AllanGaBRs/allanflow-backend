package com.allan.task.manager.board;

import com.allan.task.manager.board.dto.BoardCreateDTO;
import com.allan.task.manager.board.dto.BoardResponseDTO;
import com.allan.task.manager.board.dto.BoardUpdateDTO;
import com.allan.task.manager.config.annotation.CurrentUserId;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/workspaces/{workspaceId}/boards")
public class BoardController {

    private final BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping
    public ResponseEntity<BoardResponseDTO> create(
            @PathVariable UUID workspaceId,
            @RequestBody @Valid BoardCreateDTO dto,
            @CurrentUserId UUID requesterId
    ) {
        BoardResponseDTO response = boardService.createBoard(dto, workspaceId, requesterId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping
    public ResponseEntity<List<BoardResponseDTO>> findAll(
            @PathVariable UUID workspaceId,
            @CurrentUserId UUID requesterId
    ) {
        List<BoardResponseDTO> response = boardService.findAll(workspaceId, requesterId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/{boardId}")
    public ResponseEntity<BoardResponseDTO> findById(
            @PathVariable UUID workspaceId,
            @PathVariable UUID boardId,
            @CurrentUserId UUID requesterId
    ) {
        BoardResponseDTO response = boardService.findById(workspaceId, boardId, requesterId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PutMapping("/{boardId}")
    public ResponseEntity<BoardResponseDTO> update(
            @PathVariable UUID workspaceId,
            @PathVariable UUID boardId,
            @RequestBody @Valid BoardUpdateDTO dto,
            @CurrentUserId UUID requesterId
    ) {
        BoardResponseDTO response = boardService.update(workspaceId, boardId, dto, requesterId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @DeleteMapping("/{boardId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID workspaceId,
            @PathVariable UUID boardId,
            @CurrentUserId UUID requesterId
    ) {
        boardService.delete(workspaceId, boardId, requesterId);
        return ResponseEntity.noContent().build();
    }
}
