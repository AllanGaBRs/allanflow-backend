package com.allan.task.manager.label.exception;

import com.allan.task.manager.shared.exceptions.ResourceNotFoundException;

public class LabelNotFoundException extends ResourceNotFoundException {
    public LabelNotFoundException(String message) {
        super(message);
    }
}
