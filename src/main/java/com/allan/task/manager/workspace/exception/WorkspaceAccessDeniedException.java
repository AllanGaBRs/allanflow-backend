package com.allan.task.manager.workspace.exception;

public class WorkspaceAccessDeniedException extends RuntimeException {
    public WorkspaceAccessDeniedException(String message) {
        super(message);
    }
}
