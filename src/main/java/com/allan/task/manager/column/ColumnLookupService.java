package com.allan.task.manager.column;

import com.allan.task.manager.column.exceptions.ColumnNotFoundException;

import java.util.UUID;

public class ColumnLookupService {

    private final ColumnRepository columnRepository;

    public ColumnLookupService(ColumnRepository columnRepository) {
        this.columnRepository = columnRepository;
    }

    public Integer resolvePosition(UUID workspaceId, UUID boardId, Integer requestedPosition) {
        if (requestedPosition != null) {
            return requestedPosition;
        }

        return columnRepository.findMaxPositionByWorkspaceIdAndBoardId(workspaceId, boardId) + 1;
    }

    public ColumnModel findColumnInBoard(UUID workspaceId, UUID boardId, UUID columnId) {
        return columnRepository.findByIdAndWorkspaceIdAndBoardId(columnId, workspaceId, boardId)
                .orElseThrow(() -> new ColumnNotFoundException("Column not found"));
    }
}
