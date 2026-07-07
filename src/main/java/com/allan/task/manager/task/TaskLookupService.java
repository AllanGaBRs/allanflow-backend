package com.allan.task.manager.task;

import com.allan.task.manager.task.exception.TaskNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TaskLookupService {

    private final TaskRepository taskRepository;

    public TaskLookupService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Integer resolvePosition(UUID columnId) {
        return taskRepository.findMaxPositionByColumnId(columnId) + 1;
    }

    public TaskModel findTaskInColumn(UUID columnId, UUID taskId) {
        return taskRepository.findByIdAndColumnId(taskId, columnId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));
    }
}