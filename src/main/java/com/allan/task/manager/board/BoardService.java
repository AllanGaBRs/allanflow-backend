package com.allan.task.manager.board;

import com.allan.task.manager.board.dto.BoardCreateDTO;
import com.allan.task.manager.board.dto.BoardResponseDTO;
import com.allan.task.manager.board.dto.BoardUpdateDTO;
import com.allan.task.manager.board.exceptions.BoardAlreadyExistsException;
import com.allan.task.manager.board.exceptions.BoardNotFoundException;
import com.allan.task.manager.board.mapper.BoardMapper;
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
public class BoardService {

    private final BoardRepository boardRepository;
    private final WorkspaceRepository workspaceRepository;
    private final MembershipRepository membershipRepository;

    public BoardService(
            BoardRepository boardRepository,
            WorkspaceRepository workspaceRepository,
            MembershipRepository membershipRepository
    ) {
        this.boardRepository = boardRepository;
        this.workspaceRepository = workspaceRepository;
        this.membershipRepository = membershipRepository;
    }

    @Transactional
    public BoardResponseDTO createBoard(BoardCreateDTO dto, UUID workspaceId, UUID requesterId) {
        MembershipModel requesterMembership = findWorkspaceMembership(workspaceId, requesterId);
        Utils.validateOwnerOrAdmin(requesterMembership);

        if (boardRepository.existsByNameAndWorkspaceId(dto.name(), workspaceId)) {
            throw new BoardAlreadyExistsException("Um Board com o nome: " + dto.name() + " já existe.");
        }

        WorkspaceModel workspace = Utils.findActiveWorkspaceById(workspaceRepository, workspaceId);

        BoardModel board = new BoardModel();
        board.setName(dto.name());
        board.setDescription(dto.description());
        board.setWorkspace(workspace);

        BoardModel savedBoard = boardRepository.save(board);

        return BoardMapper.toResponse(savedBoard);
    }

    @Transactional(readOnly = true)
    public List<BoardResponseDTO> findAll(UUID workspaceId, UUID requesterId) {
        findWorkspaceMembership(workspaceId, requesterId);
        Utils.findActiveWorkspaceById(workspaceRepository, workspaceId);

        return BoardMapper.toResponseList(boardRepository.findByWorkspaceIdWithColumns(workspaceId));
    }

    @Transactional(readOnly = true)
    public BoardResponseDTO findById(UUID workspaceId, UUID boardId, UUID requesterId) {
        findWorkspaceMembership(workspaceId, requesterId);

        BoardModel board = findBoardInWorkspace(workspaceId, boardId);

        return BoardMapper.toResponse(board);
    }

    @Transactional
    public BoardResponseDTO update(
            UUID workspaceId,
            UUID boardId,
            BoardUpdateDTO dto,
            UUID requesterId
    ) {
        MembershipModel requesterMembership = findWorkspaceMembership(workspaceId, requesterId);
        Utils.validateOwnerOrAdmin(requesterMembership);

        BoardModel board = findBoardInWorkspace(workspaceId, boardId);

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
        MembershipModel requesterMembership = findWorkspaceMembership(workspaceId, requesterId);
        Utils.validateOwnerOrAdmin(requesterMembership);

        BoardModel board = findBoardInWorkspace(workspaceId, boardId);

        boardRepository.delete(board);
    }

    private BoardModel findBoardInWorkspace(UUID workspaceId, UUID boardId) {
        return boardRepository.findByIdAndWorkspaceIdWithColumns(boardId, workspaceId)
                .orElseThrow(() -> new BoardNotFoundException("Board not found"));
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
