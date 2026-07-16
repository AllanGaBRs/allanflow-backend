package com.allan.task.manager.column;

import com.allan.task.manager.board.BoardModel;
import com.allan.task.manager.board.BoardLookupService;
import com.allan.task.manager.board.BoardPermissionService;
import com.allan.task.manager.column.dto.ColumnCreateDTO;
import com.allan.task.manager.column.dto.ColumnResponseDTO;
import com.allan.task.manager.column.dto.ColumnUpdateDTO;
import com.allan.task.manager.column.mapper.ColumnMapper;
import com.allan.task.manager.workspace.WorkspaceModel;
import com.allan.task.manager.workspace.WorkspaceLookupService;
import com.allan.task.manager.workspace.WorkspacePermissionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ColumnService {

    private final ColumnRepository columnRepository;
    private final ColumnLookupService columnLookupService;
    private final BoardLookupService boardLookupService;
    private final BoardPermissionService boardPermissionService;
    private final WorkspaceLookupService workspaceLookupService;
    private final WorkspacePermissionService workspacePermissionService;

    public ColumnService(ColumnRepository columnRepository,
                         ColumnLookupService columnLookupService,
                         BoardLookupService boardLookupService,
                         BoardPermissionService boardPermissionService,
                         WorkspaceLookupService workspaceLookupService,
                         WorkspacePermissionService workspacePermissionService) {
        this.columnRepository = columnRepository;
        this.columnLookupService = columnLookupService;
        this.boardLookupService = boardLookupService;
        this.boardPermissionService = boardPermissionService;
        this.workspaceLookupService = workspaceLookupService;
        this.workspacePermissionService = workspacePermissionService;
    }

    @Transactional
    public ColumnResponseDTO create(
            UUID workspaceId,
            UUID boardId,
            ColumnCreateDTO dto,
            UUID requesterId
    ) {
        boardPermissionService.requireBoardManagement(workspaceId, requesterId);

        WorkspaceModel workspace = workspaceLookupService.findActiveById(workspaceId);
        BoardModel board = boardLookupService.findInWorkspace(workspaceId, boardId);

        ColumnModel column = new ColumnModel();
        column.setName(dto.name());
        column.setPosition(columnLookupService.resolvePosition(workspaceId, boardId, dto.position()));
        column.setWorkspace(workspace);
        column.setBoard(board);

        ColumnModel savedColumn = columnRepository.save(column);

        return ColumnMapper.toResponse(savedColumn);
    }

    @Transactional(readOnly = true)
    public List<ColumnResponseDTO> findAll(UUID workspaceId, UUID boardId, UUID requesterId) {
        boardPermissionService.requireBoardAccess(workspaceId, boardId, requesterId);
        workspaceLookupService.findActiveById(workspaceId);
        boardLookupService.findInWorkspace(workspaceId, boardId);

        return ColumnMapper.toResponseList(
                columnRepository.findByWorkspaceIdAndBoardIdOrderByPositionAsc(workspaceId, boardId)
        );
    }

    @Transactional(readOnly = true)
    public ColumnResponseDTO findById(
            UUID workspaceId,
            UUID boardId,
            UUID columnId,
            UUID requesterId
    ) {
        boardPermissionService.requireBoardAccess(workspaceId, boardId, requesterId);
        boardLookupService.findInWorkspace(workspaceId, boardId);

        ColumnModel column = columnLookupService.findColumnInBoard(workspaceId, boardId, columnId);

        return ColumnMapper.toResponse(column);
    }

    @Transactional
    public ColumnResponseDTO update(
            UUID workspaceId,
            UUID boardId,
            UUID columnId,
            ColumnUpdateDTO dto,
            UUID requesterId
    ) {
        boardPermissionService.requireBoardManagement(workspaceId, requesterId);
        boardLookupService.findInWorkspace(workspaceId, boardId);

        ColumnModel column = columnLookupService.findColumnInBoard(workspaceId, boardId, columnId);

        if (dto.name() != null && !dto.name().isBlank()) {
            column.setName(dto.name());
        }

        if (dto.position() != null) {
            column.setPosition(dto.position());
        }

        ColumnModel updatedColumn = columnRepository.save(column);

        return ColumnMapper.toResponse(updatedColumn);
    }

    @Transactional
    public void delete(UUID workspaceId, UUID boardId, UUID columnId, UUID requesterId) {
        workspacePermissionService.requireOwnerOrAdmin(workspaceId, requesterId);
        boardLookupService.findInWorkspace(workspaceId, boardId);

        ColumnModel column = columnLookupService.findColumnInBoard(workspaceId, boardId, columnId);

        columnRepository.delete(column);
    }
}
