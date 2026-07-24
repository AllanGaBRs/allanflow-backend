package com.allan.task.manager.dashboard.dto;

public record DashboardResponseDTO(
        DashboardSummaryDTO summary,
        TasksByPriorityDTO tasksByPriority
) {
}
