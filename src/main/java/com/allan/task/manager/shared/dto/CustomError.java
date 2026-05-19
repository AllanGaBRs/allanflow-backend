package com.allan.task.manager.shared.dto;

import java.time.Instant;

public record CustomError(
        Instant timestamps,
        Integer status,
        String error,
        String path
) {
}
