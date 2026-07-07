package com.allan.task.manager.client.exceptions;

import com.allan.task.manager.shared.exceptions.ResourceNotFoundException;

public class ClientNotFoundException extends ResourceNotFoundException {
    public ClientNotFoundException(String message) {
        super(message);
    }
}
