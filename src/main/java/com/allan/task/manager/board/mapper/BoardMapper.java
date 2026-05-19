package com.allan.task.manager.board.mapper;

import com.allan.task.manager.board.BoardModel;
import com.allan.task.manager.board.dto.BoardResponseDTO;
import com.allan.task.manager.column.ColumnModel;
import com.allan.task.manager.column.dto.ColumnResponseDTO;

import java.util.Comparator;
import java.util.List;

public class BoardMapper {

    private BoardMapper() {
    }

    public static BoardResponseDTO toResponse(BoardModel board) {
        return new BoardResponseDTO(
                board.getId(),
                board.getName(),
                board.getDescription(),
                toColumnResponseList(board.getColumns())
        );
    }

    public static List<BoardResponseDTO> toResponseList(List<BoardModel> boards) {
        return boards.stream()
                .map(BoardMapper::toResponse)
                .toList();
    }

    private static List<ColumnResponseDTO> toColumnResponseList(List<ColumnModel> columns) {
        if (columns == null) {
            return List.of();
        }

        return columns.stream()
                .sorted(Comparator.comparing(ColumnModel::getPosition))
                .map(column -> new ColumnResponseDTO(
                        column.getId(),
                        column.getName(),
                        column.getPosition()
                ))
                .toList();
    }
}
