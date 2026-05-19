package com.allan.task.manager.board.exceptions;

import com.allan.task.manager.shared.exceptions.ResourceNotFoundException;

public class BoardNotFoundException extends ResourceNotFoundException {
    public BoardNotFoundException(String message) {
        super(message);
    }
}
