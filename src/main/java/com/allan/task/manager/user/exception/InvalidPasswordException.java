package com.allan.task.manager.user.exception;

import com.allan.task.manager.shared.exceptions.BadRequestException;

public class InvalidPasswordException extends BadRequestException {
    public InvalidPasswordException(String message) {
        super(message);
    }
}
