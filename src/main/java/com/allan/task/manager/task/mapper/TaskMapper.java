package com.allan.task.manager.task.mapper;

import com.allan.task.manager.label.mapper.LabelMapper;
import com.allan.task.manager.task.TaskModel;
import com.allan.task.manager.task.dto.TaskResponseDTO;
import com.allan.task.manager.user.mapper.UserMapper;

import java.util.List;

public class TaskMapper {

        public static TaskResponseDTO toResponse(TaskModel task) {
                return new TaskResponseDTO(
                        task.getId(),
                        task.getTitle(),
                        task.getDescription(),

                        task.getColumn().getId(),
                        task.getColumn().getName(),

                        task.getBoard().getId(),
                        task.getBoard().getName(),

                        task.getPosition(),
                        task.getPriority(),
                        task.isArchived(),

                        task.getDueDate(),

                        task.getLabels().stream()
                                .map(LabelMapper::toResponse)
                                .collect(java.util.stream.Collectors.toSet()),

                        task.getAssignees().stream()
                                .map(UserMapper::toResponse)
                                .collect(java.util.stream.Collectors.toSet())
                );
        }

        public static List<TaskResponseDTO> toResponseList(List<TaskModel> tasks) {
                return tasks.stream()
                        .map(TaskMapper::toResponse)
                        .toList();
        }
}