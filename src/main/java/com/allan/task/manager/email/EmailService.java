package com.allan.task.manager.email;

public interface EmailService {

    void send(
            String to,
            String subject,
            String html
    );
}