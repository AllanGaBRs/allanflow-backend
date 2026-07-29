package com.allan.task.manager.auth.ratelimit;

import java.time.Duration;

public record RateLimitRule(
        String name,
        long capacity,
        Duration duration
) {
}
