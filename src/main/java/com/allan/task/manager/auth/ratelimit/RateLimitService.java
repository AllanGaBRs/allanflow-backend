package com.allan.task.manager.auth.ratelimit;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitService {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public boolean tryConsume(
            String key,
            long capacity,
            Duration duration
    ) {
        Bucket bucket = buckets.computeIfAbsent(
                key,
                ignored -> createBucket(capacity, duration)
        );

        return bucket.tryConsume(1);
    }

    private Bucket createBucket(
            long capacity,
            Duration duration
    ) {
        Bandwidth limit = Bandwidth.builder()
                .capacity(capacity)
                .refillIntervally(capacity, duration)
                .build();

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}
