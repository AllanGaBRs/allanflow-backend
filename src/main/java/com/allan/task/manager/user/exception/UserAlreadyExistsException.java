package com.allan.task.manager.user.exception;

import com.allan.task.manager.shared.exceptions.AlreadyExistsException;

public class UserAlreadyExistsException extends AlreadyExistsException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
