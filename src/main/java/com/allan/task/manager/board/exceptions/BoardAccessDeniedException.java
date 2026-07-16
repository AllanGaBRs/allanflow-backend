package com.allan.task.manager.board.exceptions;

public class BoardAccessDeniedException extends RuntimeException {
    public BoardAccessDeniedException(String message) {
        super(message);
    }
}
