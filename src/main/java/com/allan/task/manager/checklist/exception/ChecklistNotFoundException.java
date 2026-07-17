package com.allan.task.manager.checklist.exception;

import com.allan.task.manager.shared.exceptions.ResourceNotFoundException;

public class ChecklistNotFoundException extends ResourceNotFoundException {
    public ChecklistNotFoundException(String message) {
        super(message);
    }
}
