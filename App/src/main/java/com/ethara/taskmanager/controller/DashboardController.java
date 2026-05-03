package com.ethara.taskmanager.controller;

import com.ethara.taskmanager.dto.DashboardSummary;
import com.ethara.taskmanager.dto.TaskResponse;
import com.ethara.taskmanager.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/my-tasks")
    public ResponseEntity<List<TaskResponse>> getMyTasks(Authentication authentication) {
        return ResponseEntity.ok(dashboardService.getMyTasks(authentication.getName()));
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<TaskResponse>> getOverdueTasks(Authentication authentication) {
        return ResponseEntity.ok(dashboardService.getOverdueTasks(authentication.getName()));
    }

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummary> getSummary(Authentication authentication) {
        return ResponseEntity.ok(dashboardService.getSummary(authentication.getName()));
    }
}
