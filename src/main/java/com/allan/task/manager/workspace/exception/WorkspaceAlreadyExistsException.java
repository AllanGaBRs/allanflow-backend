package com.allan.task.manager.workspace.exception;

public class WorkspaceAlreadyExistsException extends RuntimeException {
    public WorkspaceAlreadyExistsException(String message) {
        super(message);
    }
}
