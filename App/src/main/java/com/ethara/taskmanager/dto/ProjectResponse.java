package com.ethara.taskmanager.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProjectResponse {
    private Long id;
    private String name;
    private String description;
    private String createdByName;
    private String createdByEmail;
    private LocalDateTime createdAt;
}
