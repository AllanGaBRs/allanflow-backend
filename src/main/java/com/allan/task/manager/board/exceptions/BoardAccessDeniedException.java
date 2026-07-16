package com.allan.task.manager.board.exceptions;

import com.allan.task.manager.workspace.exception.WorkspaceAccessDeniedException;

public class BoardAccessDeniedException extends WorkspaceAccessDeniedException {
    public BoardAccessDeniedException(String message) {
        super(message);
    }
}
