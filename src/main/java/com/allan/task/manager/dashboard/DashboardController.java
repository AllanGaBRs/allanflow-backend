package com.allan.task.manager.dashboard;

import com.allan.task.manager.config.annotation.CurrentUserId;
import com.allan.task.manager.dashboard.dto.DashboardResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/workspaces/{workspaceId}/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping
    public ResponseEntity<DashboardResponseDTO> find(
            @PathVariable UUID workspaceId,
            @CurrentUserId UUID requesterId
    ) {
        DashboardResponseDTO response = dashboardService.find(workspaceId, requesterId);
        return ResponseEntity.ok(response);
    }
}
