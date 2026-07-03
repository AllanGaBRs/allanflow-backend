package com.allan.task.manager.task.dto;

import com.allan.task.manager.task.TaskModel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record TaskCreateDTO(

        @NotBlank(message = "Title is required")
        @Size(min = 2, max = 200, message = "Title must be between 2 and 200 characters")
        String title,

        @Size(max = 5000, message = "Description cannot exceed 5000 characters")
        String description,

        TaskModel.Priority priority,

        LocalDateTime dueDate,

        Set<UUID> labels,

        Set<UUID> assignees,

        UUID client
) {
}