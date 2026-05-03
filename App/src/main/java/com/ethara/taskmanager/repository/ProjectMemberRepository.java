package com.ethara.taskmanager.repository;

import com.ethara.taskmanager.model.Project;
import com.ethara.taskmanager.model.ProjectMember;
import com.ethara.taskmanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
    List<ProjectMember> findByProject(Project project);
    List<ProjectMember> findByUser(User user);
    Optional<ProjectMember> findByUserAndProject(User user, Project project);
    boolean existsByUserAndProject(User user, Project project);
}
