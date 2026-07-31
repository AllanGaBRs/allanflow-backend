package com.allan.task.manager.board;

import com.allan.task.manager.board.dto.BoardMemberCreateDTO;
import com.allan.task.manager.board.dto.BoardMemberResponseDTO;
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
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID requesterId = UUID.fromString(jwt.getClaimAsString("userId"));

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
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID requesterId = UUID.fromString(jwt.getClaimAsString("userId"));

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
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID requesterId = UUID.fromString(jwt.getClaimAsString("userId"));

        boardMemberService.removeMember(workspaceId, boardId, userId, requesterId);

        return ResponseEntity.noContent().build();
    }
}