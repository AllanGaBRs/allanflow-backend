package com.allan.task.manager.board.dto;

import com.allan.task.manager.column.dto.ColumnResponseDTO;

import java.util.List;
import java.util.UUID;

public record BoardResponseDTO(
        UUID id,
        String name,
        String description,
        List<ColumnResponseDTO> columns
) {
}
