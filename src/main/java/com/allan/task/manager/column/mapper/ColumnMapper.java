package com.allan.task.manager.column.mapper;

import com.allan.task.manager.column.ColumnModel;
import com.allan.task.manager.column.dto.ColumnResponseDTO;

import java.util.List;

public class ColumnMapper {

    private ColumnMapper() {
    }

    public static ColumnResponseDTO toResponse(ColumnModel column) {
        return new ColumnResponseDTO(
                column.getId(),
                column.getName(),
                column.getPosition()
        );
    }

    public static List<ColumnResponseDTO> toResponseList(List<ColumnModel> columns) {
        return columns.stream()
                .map(ColumnMapper::toResponse)
                .toList();
    }
}
