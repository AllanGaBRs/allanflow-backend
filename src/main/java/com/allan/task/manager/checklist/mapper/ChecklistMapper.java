package com.allan.task.manager.checklist.mapper;

import com.allan.task.manager.checklist.ChecklistModel;
import com.allan.task.manager.checklist.dto.ChecklistItemResponseDTO;
import com.allan.task.manager.checklist.dto.ChecklistResponseDTO;
import com.allan.task.manager.checklistitem.ChecklistItemModel;

import java.util.Comparator;
import java.util.List;

public class ChecklistMapper {

    public static ChecklistItemResponseDTO toItemResponse(ChecklistItemModel item) {
        return new ChecklistItemResponseDTO(
                item.getId(),
                item.getContent(),
                item.getChecked(),
                item.getPosition()
        );
    }

    public static ChecklistResponseDTO toResponse(ChecklistModel checklist) {
        return new ChecklistResponseDTO(
                checklist.getId(),
                checklist.getTitle(),
                checklist.getTask().getId(),
                checklist.getItems()
                        .stream()
                        .sorted(Comparator.comparing(ChecklistItemModel::getPosition))
                        .map(ChecklistMapper::toItemResponse)
                        .toList()
        );
    }

    public static List<ChecklistResponseDTO> toResponseList(List<ChecklistModel> checklists) {
        return checklists.stream()
                .map(ChecklistMapper::toResponse)
                .toList();
    }
}
