package com.allan.task.manager.comment.exception;

import com.allan.task.manager.shared.exceptions.ResourceNotFoundException;

public class CommentNotFoundException extends ResourceNotFoundException {
    public CommentNotFoundException(String message) {
        super(message);
    }
}
