package com.allan.task.manager.passwordreset.exception;

import com.allan.task.manager.shared.exceptions.BadRequestException;

public class InvalidPasswordResetException extends BadRequestException {

    public InvalidPasswordResetException() {
        super("Invalid or expired code");
    }
}
