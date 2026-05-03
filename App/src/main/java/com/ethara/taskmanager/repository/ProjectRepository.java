package com.ethara.taskmanager.repository;

import com.ethara.taskmanager.model.Project;
import com.ethara.taskmanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByCreatedBy(User createdBy);
    @Query("SELECT p FROM Project p WHERE p.createdBy = :user OR p.id IN " +
           "(SELECT pm.project.id FROM ProjectMember pm WHERE pm.user = :user)")
    List<Project> findAllByUser(@Param("user") User user);
}
