package com.allan.task.manager.board.exceptions;

import com.allan.task.manager.shared.exceptions.AlreadyExistsException;

public class BoardAlreadyExistsException extends AlreadyExistsException {
    public BoardAlreadyExistsException(String message) {
        super(message);
    }
}