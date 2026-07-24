package com.allan.task.manager.dashboard.dto;

public record TasksByPriorityDTO(
        long low,
        long medium,
        long high
) {
}
