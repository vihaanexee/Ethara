package com.ethara.taskmanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TaskRequest {
    @NotBlank(message = "Task title is required") private String title;
    private String description;
    private String dueDate;
    private Long assignedToId;
    @NotNull(message = "Project ID is required") private Long projectId;
}
