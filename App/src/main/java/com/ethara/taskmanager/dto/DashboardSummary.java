package com.ethara.taskmanager.dto;

import lombok.*;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DashboardSummary {
    private long totalTasks;
    private long completedTasks;
    private long pendingTasks;
    private long overdueTasks;
    private Map<String, Long> tasksByStatus;
    private long totalProjects;
}
