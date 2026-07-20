package com.allan.task.manager.workspace.exception;

import com.allan.task.manager.shared.exceptions.AlreadyExistsException;

public class WorkspaceAlreadyExistsException extends AlreadyExistsException {
    public WorkspaceAlreadyExistsException(String message) {
        super(message);
    }
}
