package com.allan.task.manager.user.exception;

import com.allan.task.manager.shared.exceptions.ResourceNotFoundException;

public class UserNotFoundException extends ResourceNotFoundException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
