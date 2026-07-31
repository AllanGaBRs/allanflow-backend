package com.allan.task.manager.checklist;

import com.allan.task.manager.checklist.dto.ChecklistCreateDTO;
import com.allan.task.manager.checklist.dto.ChecklistItemCreateDTO;
import com.allan.task.manager.checklist.dto.ChecklistItemResponseDTO;
import com.allan.task.manager.checklist.dto.ChecklistItemUpdateDTO;
import com.allan.task.manager.checklist.dto.ChecklistResponseDTO;
import com.allan.task.manager.checklist.dto.ChecklistUpdateDTO;
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
@RequestMapping("/workspaces/{workspaceId}/boards/{boardId}/columns/{columnId}/tasks/{taskId}/checklists")
public class ChecklistController {

    private final ChecklistService checklistService;

    public ChecklistController(ChecklistService checklistService) {
        this.checklistService = checklistService;
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping
    public ResponseEntity<ChecklistResponseDTO> create(
            @PathVariable UUID workspaceId,
            @PathVariable UUID boardId,
            @PathVariable UUID columnId,
            @PathVariable UUID taskId,
            @RequestBody @Valid ChecklistCreateDTO dto,
            @CurrentUserId UUID requesterId
    ) {
        ChecklistResponseDTO response = checklistService.create(
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
    @GetMapping
    public ResponseEntity<List<ChecklistResponseDTO>> findAllByTask(
            @PathVariable UUID workspaceId,
            @PathVariable UUID boardId,
            @PathVariable UUID columnId,
            @PathVariable UUID taskId,
            @CurrentUserId UUID requesterId
    ) {
        List<ChecklistResponseDTO> response = checklistService.findAllByTask(
                workspaceId,
                boardId,
                columnId,
                taskId,
                requesterId
        );
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PutMapping("/{checklistId}")
    public ResponseEntity<ChecklistResponseDTO> update(
            @PathVariable UUID workspaceId,
            @PathVariable UUID boardId,
            @PathVariable UUID columnId,
            @PathVariable UUID taskId,
            @PathVariable UUID checklistId,
            @RequestBody @Valid ChecklistUpdateDTO dto,
            @CurrentUserId UUID requesterId
    ) {
        ChecklistResponseDTO response = checklistService.update(
                workspaceId,
                boardId,
                columnId,
                taskId,
                checklistId,
                dto,
                requesterId
        );
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @DeleteMapping("/{checklistId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID workspaceId,
            @PathVariable UUID boardId,
            @PathVariable UUID columnId,
            @PathVariable UUID taskId,
            @PathVariable UUID checklistId,
            @CurrentUserId UUID requesterId
   ) {
        checklistService.delete(
                workspaceId,
                boardId,
                columnId,
                taskId,
                checklistId,
                requesterId
        );
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping("/{checklistId}/items")
    public ResponseEntity<ChecklistItemResponseDTO> createItem(
            @PathVariable UUID workspaceId,
            @PathVariable UUID boardId,
            @PathVariable UUID columnId,
            @PathVariable UUID taskId,
            @PathVariable UUID checklistId,
            @RequestBody @Valid ChecklistItemCreateDTO dto,
            @CurrentUserId UUID requesterId
    ) {
        ChecklistItemResponseDTO response = checklistService.createItem(
                workspaceId,
                boardId,
                columnId,
                taskId,
                checklistId,
                dto,
                requesterId
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PatchMapping("/{checklistId}/items/{itemId}")
    public ResponseEntity<ChecklistItemResponseDTO> updateItem(
            @PathVariable UUID workspaceId,
            @PathVariable UUID boardId,
            @PathVariable UUID columnId,
            @PathVariable UUID taskId,
            @PathVariable UUID checklistId,
            @PathVariable UUID itemId,
            @RequestBody @Valid ChecklistItemUpdateDTO dto,
            @CurrentUserId UUID requesterId
    ) {
          ChecklistItemResponseDTO response = checklistService.updateItem(
                workspaceId,
                boardId,
                columnId,
                taskId,
                checklistId,
                itemId,
                dto,
                requesterId
        );
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @DeleteMapping("/{checklistId}/items/{itemId}")
    public ResponseEntity<Void> deleteItem(
            @PathVariable UUID workspaceId,
            @PathVariable UUID boardId,
            @PathVariable UUID columnId,
            @PathVariable UUID taskId,
            @PathVariable UUID checklistId,
            @PathVariable UUID itemId,
            @CurrentUserId UUID requesterId
    ) {
        checklistService.deleteItem(
                workspaceId,
                boardId,
                columnId,
                taskId,
                checklistId,
                itemId,
                requesterId
        );
        return ResponseEntity.noContent().build();
    }
}
