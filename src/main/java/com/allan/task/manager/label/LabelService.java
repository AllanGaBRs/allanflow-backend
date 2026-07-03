package com.allan.task.manager.label;

import com.allan.task.manager.board.BoardLookupService;
import com.allan.task.manager.board.BoardModel;
import com.allan.task.manager.label.dto.LabelCreateDTO;
import com.allan.task.manager.label.dto.LabelResponseDTO;
import com.allan.task.manager.label.mapper.LabelMapper;
import com.allan.task.manager.workspace.WorkspacePermissionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class LabelService {

    private final LabelRepository labelRepository;
    private final BoardLookupService boardLookupService;
    private final WorkspacePermissionService workspacePermissionService;

    public LabelService(
            LabelRepository labelRepository,
            BoardLookupService boardLookupService,
            WorkspacePermissionService workspacePermissionService
    ) {
        this.labelRepository = labelRepository;
        this.boardLookupService = boardLookupService;
        this.workspacePermissionService = workspacePermissionService;
    }

    @Transactional
    public LabelResponseDTO create(
            UUID workspaceId,
            UUID boardId,
            LabelCreateDTO dto,
            UUID requesterId
    ) {
        workspacePermissionService.requireOwnerOrAdmin(workspaceId, requesterId);

        BoardModel board = boardLookupService.findInWorkspace(workspaceId, boardId);

        LabelModel label = new LabelModel();
        label.setName(dto.name());
        label.setColor(dto.color());
        label.setBoard(board);

        LabelModel saved = labelRepository.save(label);

        return LabelMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public LabelResponseDTO findById(
            UUID workspaceId,
            UUID boardId,
            UUID labelId,
            UUID requesterId
    ) {
        workspacePermissionService.requireMember(workspaceId, requesterId);

        boardLookupService.findInWorkspace(workspaceId, boardId);

        LabelModel label = labelRepository
                .findByIdAndBoardId(labelId, boardId)
                .orElseThrow(() -> new RuntimeException("Label not found"));

        return LabelMapper.toResponse(label);
    }

    @Transactional(readOnly = true)
    public List<LabelResponseDTO> findAll(
            UUID workspaceId,
            UUID boardId,
            UUID requesterId
    ) {
        workspacePermissionService.requireMember(workspaceId, requesterId);

        boardLookupService.findInWorkspace(workspaceId, boardId);

        return LabelMapper.toResponseList(
                labelRepository.findByBoardId(boardId)
        );
    }
}