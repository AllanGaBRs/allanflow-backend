package com.allan.task.manager.passwordreset;

public class InvalidPasswordResetException extends RuntimeException {

    public InvalidPasswordResetException() {
        super("Código inválido ou expirado");
    }
}