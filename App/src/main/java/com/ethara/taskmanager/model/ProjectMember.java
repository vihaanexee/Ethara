package com.ethara.taskmanager.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "project_members", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "project_id"})})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProjectMember {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false) private User user;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "project_id", nullable = false) private Project project;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private MemberRole role;
    public enum MemberRole { ADMIN, MEMBER }
}
