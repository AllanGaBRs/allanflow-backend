package com.allan.task.manager.document.exception;

import com.allan.task.manager.shared.exceptions.BadRequestException;

public class InvalidDocumentOperationException extends BadRequestException {
    public InvalidDocumentOperationException(String message) {
        super(message);
    }
}