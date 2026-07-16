package com.allan.task.manager.board.exceptions;

import com.allan.task.manager.shared.exceptions.AlreadyExistsException;

public class BoardMemberAlreadyExistsException extends AlreadyExistsException {
    public BoardMemberAlreadyExistsException(String message) {
        super(message);
    }
}
