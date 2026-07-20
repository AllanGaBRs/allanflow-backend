package com.allan.task.manager.workspace.exception;

import com.allan.task.manager.shared.exceptions.ForbiddenException;

public class WorkspaceAccessDeniedException extends ForbiddenException {
    public WorkspaceAccessDeniedException(String message) {
        super(message);
    }
}
