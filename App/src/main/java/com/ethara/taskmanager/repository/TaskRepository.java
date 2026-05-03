package com.ethara.taskmanager.repository;

import com.ethara.taskmanager.model.Project;
import com.ethara.taskmanager.model.Task;
import com.ethara.taskmanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByProject(Project project);
    List<Task> findByAssignedTo(User assignedTo);
    List<Task> findByAssignedToAndDueDateBeforeAndStatusNot(User assignedTo, LocalDate date, Task.TaskStatus status);
    @Query("SELECT t.status, COUNT(t) FROM Task t WHERE t.assignedTo = :user GROUP BY t.status")
    List<Object[]> countTasksByStatusForUser(@Param("user") User user);
    @Query("SELECT COUNT(t) FROM Task t WHERE t.assignedTo = :user")
    long countByAssignedTo(@Param("user") User user);
    @Query("SELECT COUNT(t) FROM Task t WHERE t.assignedTo = :user AND t.status = :status")
    long countByAssignedToAndStatus(@Param("user") User user, @Param("status") Task.TaskStatus status);
    List<Task> findByProjectAndStatus(Project project, Task.TaskStatus status);
}
