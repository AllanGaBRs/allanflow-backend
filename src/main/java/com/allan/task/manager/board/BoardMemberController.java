package com.allan.task.manager.board;

import com.allan.task.manager.board.dto.BoardMemberCreateDTO;
import com.allan.task.manager.board.dto.BoardMemberResponseDTO;
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
@RequestMapping("/workspaces/{workspaceId}/boards/{boardId}/members")
public class BoardMemberController {

    private final BoardMemberService boardMemberService;

    public BoardMemberController(BoardMemberService boardMemberService) {
        this.boardMemberService = boardMemberService;
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping
    public ResponseEntity<List<BoardMemberResponseDTO>> findMembers(
            @PathVariable UUID workspaceId,
            @PathVariable UUID boardId,
            @CurrentUserId UUID requesterId
    ) {
        return ResponseEntity.ok(
                boardMemberService.findMembers(workspaceId, boardId, requesterId)
        );
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping
    public ResponseEntity<BoardMemberResponseDTO> addMember(
            @PathVariable UUID workspaceId,
            @PathVariable UUID boardId,
            @RequestBody @Valid BoardMemberCreateDTO dto,
            @CurrentUserId UUID requesterId
    ) {
        BoardMemberResponseDTO response = boardMemberService.addMember(
                workspaceId,
                boardId,
                dto.userId(),
                requesterId
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable UUID workspaceId,
            @PathVariable UUID boardId,
            @PathVariable UUID userId,
            @CurrentUserId UUID requesterId
    ) {
        boardMemberService.removeMember(workspaceId, boardId, userId, requesterId);
        return ResponseEntity.noContent().build();
    }
}