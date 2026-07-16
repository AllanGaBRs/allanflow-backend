package com.allan.task.manager.board.exceptions;

import com.allan.task.manager.shared.exceptions.ResourceNotFoundException;

public class BoardMemberNotFoundException extends ResourceNotFoundException {
    public BoardMemberNotFoundException(String message) {
        super(message);
    }
}
