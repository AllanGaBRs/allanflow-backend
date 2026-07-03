package com.allan.task.manager.task.mapper;

import com.allan.task.manager.task.TaskModel;
import com.allan.task.manager.task.dto.TaskResponseDTO;

import java.util.List;

public class TaskMapper {

        public static TaskResponseDTO toResponse(TaskModel task) {
                return new TaskResponseDTO(
                        task.getId(),
                        task.getTitle(),
                        task.getDescription()
                );
        }

        public static List<TaskResponseDTO> toResponseList(List<TaskModel> tasks) {
                return tasks.stream()
                        .map(TaskMapper::toResponse)
                        .toList();
        }
}