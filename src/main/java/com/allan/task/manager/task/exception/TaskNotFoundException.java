package com.allan.task.manager.task.exception;

import com.allan.task.manager.shared.exceptions.ResourceNotFoundException;

public class TaskNotFoundException extends ResourceNotFoundException {
    public TaskNotFoundException(String message) {
        super(message);
    }
}
