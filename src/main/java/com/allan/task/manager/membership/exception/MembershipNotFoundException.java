package com.allan.task.manager.membership.exception;

import com.allan.task.manager.shared.exceptions.ResourceNotFoundException;

public class MembershipNotFoundException extends ResourceNotFoundException {
    public MembershipNotFoundException(String message) {
        super(message);
    }
}
