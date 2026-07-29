package com.allan.task.manager.auth.ratelimit;

import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RateLimitRuleResolver {

    public RateLimitRule resolve(
            String method,
            String path
    ) {
        if ("POST".equals(method) && "/auth/login".equals(path)) {
            return new RateLimitRule(
                    "login",
                    10,
                    Duration.ofMinutes(1)
            );
        }

        if ("POST".equals(method) && "/auth/forgot-password".equals(path)) {
            return new RateLimitRule(
                    "forgot-password",
                    3,
                    Duration.ofMinutes(15)
            );
        }

        if ("POST".equals(method) && "/auth/reset-password".equals(path)) {
            return new RateLimitRule(
                    "reset-password",
                    5,
                    Duration.ofMinutes(15)
            );
        }

        if ("POST".equals(method) && "/users".equals(path)) {
            return new RateLimitRule(
                    "register",
                    5,
                    Duration.ofHours(1)
            );
        }

        return null;
    }
}