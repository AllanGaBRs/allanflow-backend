package com.allan.task.manager.dashboard.dto;

public record DashboardSummaryDTO(
        long boards,
        long activeTasks,
        long overdueTasks,
        long members
) {
}
