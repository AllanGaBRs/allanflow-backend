package com.allan.task.manager.board;

import com.allan.task.manager.board.dto.BoardCreateDTO;
import com.allan.task.manager.board.dto.BoardResponseDTO;
import com.allan.task.manager.board.dto.BoardUpdateDTO;
import com.allan.task.manager.board.exceptions.BoardAlreadyExistsException;
import com.allan.task.manager.board.mapper.BoardMapper;
import com.allan.task.manager.membership.MembershipModel;
import com.allan.task.manager.workspace.WorkspaceModel;
import com.allan.task.manager.workspace.WorkspaceLookupService;
import com.allan.task.manager.workspace.WorkspacePermissionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardLookupService boardLookupService;
    private final WorkspaceLookupService workspaceLookupService;
    private final WorkspacePermissionService workspacePermissionService;

    public BoardService(
            BoardRepository boardRepository,
            BoardLookupService boardLookupService,
            WorkspaceLookupService workspaceLookupService,
            WorkspacePermissionService workspacePermissionService
    ) {
        this.boardRepository = boardRepository;
        this.boardLookupService = boardLookupService;
        this.workspaceLookupService = workspaceLookupService;
        this.workspacePermissionService = workspacePermissionService;
    }

    @Transactional
    public BoardResponseDTO createBoard(BoardCreateDTO dto, UUID workspaceId, UUID requesterId) {
        workspacePermissionService.requireOwnerOrAdmin(workspaceId, requesterId);

        if (boardRepository.existsByNameAndWorkspaceId(dto.name(), workspaceId)) {
            throw new BoardAlreadyExistsException("Um Board com o nome: " + dto.name() + " já existe.");
        }

        WorkspaceModel workspace = workspaceLookupService.findActiveById(workspaceId);

        BoardModel board = new BoardModel();
        board.setName(dto.name());
        board.setDescription(dto.description());
        board.setWorkspace(workspace);

        BoardModel savedBoard = boardRepository.save(board);

        return BoardMapper.toResponse(savedBoard);
    }

    @Transactional(readOnly = true)
    public List<BoardResponseDTO> findAll(UUID workspaceId, UUID requesterId) {
        MembershipModel membership = workspacePermissionService.requireMember(workspaceId, requesterId);
        workspaceLookupService.findActiveById(workspaceId);

        List<BoardModel> boards = isWorkspaceOwnerOrAdmin(membership)
                ? boardRepository.findByWorkspaceIdWithColumns(workspaceId)
                : boardRepository.findByWorkspaceIdAndMemberIdWithColumns(workspaceId, requesterId);

        return BoardMapper.toResponseList(boards);
    }

    @Transactional(readOnly = true)
    public BoardResponseDTO findById(UUID workspaceId, UUID boardId, UUID requesterId) {
        workspacePermissionService.requireMember(workspaceId, requesterId);

        BoardModel board = boardLookupService.findInWorkspaceWithColumns(workspaceId, boardId);

        return BoardMapper.toResponse(board);
    }

    @Transactional
    public BoardResponseDTO update(
            UUID workspaceId,
            UUID boardId,
            BoardUpdateDTO dto,
            UUID requesterId
    ) {
        workspacePermissionService.requireOwnerOrAdmin(workspaceId, requesterId);

        BoardModel board = boardLookupService.findInWorkspaceWithColumns(workspaceId, boardId);

        if (dto.name() != null && !dto.name().isBlank() && !dto.name().equals(board.getName())) {
            if (boardRepository.existsByNameAndWorkspaceIdAndIdNot(dto.name(), workspaceId, boardId)) {
                throw new BoardAlreadyExistsException("Um Board com o nome: " + dto.name() + " já existe.");
            }

            board.setName(dto.name());
        }

        if (dto.description() != null) {
            board.setDescription(dto.description());
        }

        BoardModel updatedBoard = boardRepository.save(board);

        return BoardMapper.toResponse(updatedBoard);
    }

    @Transactional
    public void delete(UUID workspaceId, UUID boardId, UUID requesterId) {
        workspacePermissionService.requireOwnerOrAdmin(workspaceId, requesterId);

        BoardModel board = boardLookupService.findInWorkspaceWithColumns(workspaceId, boardId);

        boardRepository.delete(board);
    }

    private boolean isWorkspaceOwnerOrAdmin(MembershipModel membership) {
        return membership.getRole() == MembershipModel.MembershipRole.OWNER
                || membership.getRole() == MembershipModel.MembershipRole.ADMIN;
    }
}
