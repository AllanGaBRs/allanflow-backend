package com.allan.task.manager.comment.exception;

import java.nio.file.AccessDeniedException;

public class CommentAccessDeniedException extends RuntimeException {

    public CommentAccessDeniedException(String message) {
        super(message);
    }
}
