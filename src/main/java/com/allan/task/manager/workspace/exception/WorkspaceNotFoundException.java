package com.allan.task.manager.workspace.exception;

import com.allan.task.manager.shared.ResourceNotFoundException;

public class WorkspaceNotFoundException extends ResourceNotFoundException {
    public WorkspaceNotFoundException(String message) {
        super(message);
    }
}
