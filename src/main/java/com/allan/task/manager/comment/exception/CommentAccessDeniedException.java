package com.allan.task.manager.comment.exception;

import com.allan.task.manager.shared.exceptions.ForbiddenException;

public class CommentAccessDeniedException extends ForbiddenException {

    public CommentAccessDeniedException(String message) {
        super(message);
    }
}
