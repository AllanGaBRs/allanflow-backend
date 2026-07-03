package com.allan.task.manager.label.mapper;

import com.allan.task.manager.label.LabelModel;
import com.allan.task.manager.label.dto.LabelResponseDTO;

import java.util.List;

public class LabelMapper {

    public static LabelResponseDTO toResponse(LabelModel label) {
        return new LabelResponseDTO(
                label.getId(),
                label.getName(),
                label.getColor()
        );
    }

    public static List<LabelResponseDTO> toResponseList(List<LabelModel> labels) {
        return labels.stream()
                .map(LabelMapper::toResponse)
                .toList();
    }
}