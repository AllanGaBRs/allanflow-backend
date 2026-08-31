package com.allan.task.manager.document.exception;

import com.allan.task.manager.shared.exceptions.ResourceNotFoundException;

public class DocumentNotFoundException extends ResourceNotFoundException {
    public DocumentNotFoundException(String message) {
        super(message);
    }
}
