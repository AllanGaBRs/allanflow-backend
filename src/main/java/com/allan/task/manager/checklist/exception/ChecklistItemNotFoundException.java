package com.allan.task.manager.checklist.exception;

import com.allan.task.manager.shared.exceptions.ResourceNotFoundException;

public class ChecklistItemNotFoundException extends ResourceNotFoundException {
    public ChecklistItemNotFoundException(String message) {
        super(message);
    }
}
