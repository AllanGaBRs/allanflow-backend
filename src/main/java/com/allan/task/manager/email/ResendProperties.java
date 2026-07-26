package com.allan.task.manager.email;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "email.resend")
public record ResendProperties(
        String apiKey,
        String from
) {
}