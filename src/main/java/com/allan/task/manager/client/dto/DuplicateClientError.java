package com.allan.task.manager.client.dto;

import java.time.Instant;
import java.util.List;

public record DuplicateClientError(
        Instant timestamp,
        Integer status,
        String error,
        String path,
        List<ClientResponseDTO> duplicates
) {}
