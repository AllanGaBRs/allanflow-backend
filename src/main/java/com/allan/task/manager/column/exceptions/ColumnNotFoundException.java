package com.allan.task.manager.column.exceptions;

import com.allan.task.manager.shared.exceptions.ResourceNotFoundException;

public class ColumnNotFoundException extends ResourceNotFoundException {
    public ColumnNotFoundException(String message) {
        super(message);
    }
}
