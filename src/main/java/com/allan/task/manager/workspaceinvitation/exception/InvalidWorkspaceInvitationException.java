package com.allan.task.manager.workspaceinvitation.exception;

import com.allan.task.manager.shared.exceptions.BadRequestException;

public class InvalidWorkspaceInvitationException extends BadRequestException {

    public InvalidWorkspaceInvitationException() {
        super("Invitation is invalid, expired, or has already been used");
    }

    public InvalidWorkspaceInvitationException(String message) {
        super(message);
    }
}
