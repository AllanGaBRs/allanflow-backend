package com.allan.task.manager.board;

import com.allan.task.manager.board.exceptions.BoardNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class BoardLookupService {

    private final BoardRepository boardRepository;

    public BoardLookupService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public BoardModel findInWorkspace(UUID workspaceId, UUID boardId) {
        return boardRepository.findByIdAndWorkspaceId(boardId, workspaceId)
                .orElseThrow(() -> new BoardNotFoundException("Board not found"));
    }

    public BoardModel findInWorkspaceWithColumns(UUID workspaceId, UUID boardId) {
        return boardRepository.findByIdAndWorkspaceIdWithColumns(boardId, workspaceId)
                .orElseThrow(() -> new BoardNotFoundException("Board not found"));
    }

    public BoardModel findInWorkspaceWithMembers(UUID workspaceId, UUID boardId) {
        return boardRepository.findByIdAndWorkspaceIdWithMembers(boardId, workspaceId)
                .orElseThrow(() -> new BoardNotFoundException("Board not found"));
    }
}
