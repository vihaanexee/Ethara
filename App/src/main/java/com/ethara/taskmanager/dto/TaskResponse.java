package com.ethara.taskmanager.dto;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private String status;
    private LocalDate dueDate;
    private String assignedToName;
    private String assignedToEmail;
    private Long projectId;
    private String projectName;
    private String createdByName;
    private LocalDateTime createdAt;
    private boolean overdue;
}
