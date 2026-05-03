package com.ethara.taskmanager.service;

import com.ethara.taskmanager.dto.DashboardSummary;
import com.ethara.taskmanager.dto.TaskResponse;
import com.ethara.taskmanager.exception.ResourceNotFoundException;
import com.ethara.taskmanager.model.Task;
import com.ethara.taskmanager.model.User;
import com.ethara.taskmanager.repository.ProjectRepository;
import com.ethara.taskmanager.repository.TaskRepository;
import com.ethara.taskmanager.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    public DashboardService(TaskRepository taskRepository, UserRepository userRepository,
                            ProjectRepository projectRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
    }

    public List<TaskResponse> getMyTasks(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return taskRepository.findByAssignedTo(user).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<TaskResponse> getOverdueTasks(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return taskRepository.findByAssignedToAndDueDateBeforeAndStatusNot(user, LocalDate.now(), Task.TaskStatus.DONE)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public DashboardSummary getSummary(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        long totalTasks = taskRepository.countByAssignedTo(user);
        long completedTasks = taskRepository.countByAssignedToAndStatus(user, Task.TaskStatus.DONE);
        long pendingTasks = totalTasks - completedTasks;
        List<Task> overdueTasks = taskRepository.findByAssignedToAndDueDateBeforeAndStatusNot(
                user, LocalDate.now(), Task.TaskStatus.DONE);
        Map<String, Long> tasksByStatus = new HashMap<>();
        List<Object[]> statusCounts = taskRepository.countTasksByStatusForUser(user);
        for (Object[] row : statusCounts) {
            Task.TaskStatus status = (Task.TaskStatus) row[0];
            Long count = (Long) row[1];
            tasksByStatus.put(status.name(), count);
        }
        long totalProjects = projectRepository.findAllByUser(user).size();
        return DashboardSummary.builder().totalTasks(totalTasks).completedTasks(completedTasks)
                .pendingTasks(pendingTasks).overdueTasks(overdueTasks.size())
                .tasksByStatus(tasksByStatus).totalProjects(totalProjects).build();
    }

    private TaskResponse mapToResponse(Task task) {
        boolean overdue = task.getDueDate() != null && task.getDueDate().isBefore(LocalDate.now())
                && task.getStatus() != Task.TaskStatus.DONE;
        return TaskResponse.builder().id(task.getId()).title(task.getTitle()).description(task.getDescription())
                .status(task.getStatus().name()).dueDate(task.getDueDate())
                .assignedToName(task.getAssignedTo() != null ? task.getAssignedTo().getName() : "Unassigned")
                .assignedToEmail(task.getAssignedTo() != null ? task.getAssignedTo().getEmail() : null)
                .projectId(task.getProject().getId()).projectName(task.getProject().getName())
                .createdByName(task.getCreatedBy().getName()).createdAt(task.getCreatedAt()).overdue(overdue).build();
    }
}
