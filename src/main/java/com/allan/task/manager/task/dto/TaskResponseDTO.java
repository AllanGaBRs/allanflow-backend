package com.allan.task.manager.task.dto;

import com.allan.task.manager.client.dto.ClientResponseDTO;
import com.allan.task.manager.label.dto.LabelResponseDTO;
import com.allan.task.manager.task.TaskModel;
import com.allan.task.manager.user.dto.UserResponseDTO;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record TaskResponseDTO(
        UUID id,
        String title,
        String description,

        UUID columnId,
        String columnName,

        UUID boardId,
        String boardName,

        Integer position,
        TaskModel.Priority priority,
        boolean archived,

        LocalDateTime dueDate,

        Set<LabelResponseDTO> labels,
        Set<UserResponseDTO> assignees,

        ClientResponseDTO client
) {
}