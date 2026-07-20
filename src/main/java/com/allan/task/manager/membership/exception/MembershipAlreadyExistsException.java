package com.allan.task.manager.membership.exception;

import com.allan.task.manager.shared.exceptions.AlreadyExistsException;

public class MembershipAlreadyExistsException extends AlreadyExistsException {
    public MembershipAlreadyExistsException(String message) {
        super(message);
    }
}
