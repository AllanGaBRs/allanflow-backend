package com.allan.task.manager.column;

import com.allan.task.manager.board.BoardModel;
import com.allan.task.manager.board.BoardRepository;
import com.allan.task.manager.board.exceptions.BoardNotFoundException;
import com.allan.task.manager.column.dto.ColumnCreateDTO;
import com.allan.task.manager.column.dto.ColumnResponseDTO;
import com.allan.task.manager.column.dto.ColumnUpdateDTO;
import com.allan.task.manager.column.exceptions.ColumnNotFoundException;
import com.allan.task.manager.column.mapper.ColumnMapper;
import com.allan.task.manager.membership.MembershipModel;
import com.allan.task.manager.membership.MembershipRepository;
import com.allan.task.manager.shared.Utils;
import com.allan.task.manager.workspace.WorkspaceModel;
import com.allan.task.manager.workspace.WorkspaceRepository;
import com.allan.task.manager.workspace.exception.WorkspaceAccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ColumnService {

    private final ColumnRepository columnRepository;
    private final BoardRepository boardRepository;
    private final WorkspaceRepository workspaceRepository;
    private final MembershipRepository membershipRepository;

    public ColumnService(
            ColumnRepository columnRepository,
            BoardRepository boardRepository,
            WorkspaceRepository workspaceRepository,
            MembershipRepository membershipRepository
    ) {
        this.columnRepository = columnRepository;
        this.boardRepository = boardRepository;
        this.workspaceRepository = workspaceRepository;
        this.membershipRepository = membershipRepository;
    }

    @Transactional
    public ColumnResponseDTO create(
            UUID workspaceId,
            UUID boardId,
            ColumnCreateDTO dto,
            UUID requesterId
    ) {
        MembershipModel requesterMembership = findWorkspaceMembership(workspaceId, requesterId);
        Utils.validateOwnerOrAdmin(requesterMembership);

        WorkspaceModel workspace = Utils.findActiveWorkspaceById(workspaceRepository, workspaceId);
        BoardModel board = findBoardInWorkspace(workspaceId, boardId);

        ColumnModel column = new ColumnModel();
        column.setName(dto.name());
        column.setPosition(resolvePosition(workspaceId, boardId, dto.position()));
        column.setWorkspace(workspace);
        column.setBoard(board);

        ColumnModel savedColumn = columnRepository.save(column);

        return ColumnMapper.toResponse(savedColumn);
    }

    @Transactional(readOnly = true)
    public List<ColumnResponseDTO> findAll(UUID workspaceId, UUID boardId, UUID requesterId) {
        findWorkspaceMembership(workspaceId, requesterId);
        Utils.findActiveWorkspaceById(workspaceRepository, workspaceId);
        findBoardInWorkspace(workspaceId, boardId);

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
        findWorkspaceMembership(workspaceId, requesterId);
        findBoardInWorkspace(workspaceId, boardId);

        ColumnModel column = findColumnInBoard(workspaceId, boardId, columnId);

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
        MembershipModel requesterMembership = findWorkspaceMembership(workspaceId, requesterId);
        Utils.validateOwnerOrAdmin(requesterMembership);
        findBoardInWorkspace(workspaceId, boardId);

        ColumnModel column = findColumnInBoard(workspaceId, boardId, columnId);

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
        MembershipModel requesterMembership = findWorkspaceMembership(workspaceId, requesterId);
        Utils.validateOwnerOrAdmin(requesterMembership);
        findBoardInWorkspace(workspaceId, boardId);

        ColumnModel column = findColumnInBoard(workspaceId, boardId, columnId);

        columnRepository.delete(column);
    }

    private Integer resolvePosition(UUID workspaceId, UUID boardId, Integer requestedPosition) {
        if (requestedPosition != null) {
            return requestedPosition;
        }

        return columnRepository.findMaxPositionByWorkspaceIdAndBoardId(workspaceId, boardId) + 1;
    }

    private BoardModel findBoardInWorkspace(UUID workspaceId, UUID boardId) {
        return Utils.findBoardInWorkspace(
                boardRepository,
                workspaceId,
                boardId,
                () -> new BoardNotFoundException("Board not found")
        );
    }

    private ColumnModel findColumnInBoard(UUID workspaceId, UUID boardId, UUID columnId) {
        return columnRepository.findByIdAndWorkspaceIdAndBoardId(columnId, workspaceId, boardId)
                .orElseThrow(() -> new ColumnNotFoundException("Column not found"));
    }

    private MembershipModel findWorkspaceMembership(UUID workspaceId, UUID requesterId) {
        return Utils.findMembership(
                membershipRepository,
                workspaceId,
                requesterId,
                () -> new WorkspaceAccessDeniedException("You do not have access to this workspace")
        );
    }
}
