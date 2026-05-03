package com.ethara.taskmanager.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Task {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false) private String title;
    @Column(length = 2000) private String description;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private TaskStatus status;
    @Column(name = "due_date") private LocalDate dueDate;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "assigned_to") private User assignedTo;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "project_id", nullable = false) private Project project;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "created_by", nullable = false) private User createdBy;
    @Column(name = "created_at", nullable = false, updatable = false) private LocalDateTime createdAt;
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) this.status = TaskStatus.TODO;
    }
    public enum TaskStatus { TODO, IN_PROGRESS, IN_REVIEW, DONE }
}
