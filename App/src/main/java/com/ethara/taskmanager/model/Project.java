package com.ethara.taskmanager.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "projects")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Project {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false) private String name;
    @Column(length = 1000) private String description;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "created_by", nullable = false) private User createdBy;
    @Column(name = "created_at", nullable = false, updatable = false) private LocalDateTime createdAt;
    @PrePersist
    protected void onCreate() { this.createdAt = LocalDateTime.now(); }
}
