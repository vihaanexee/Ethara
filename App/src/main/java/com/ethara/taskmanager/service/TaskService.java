package com.ethara.taskmanager.service;

import com.ethara.taskmanager.dto.StatusUpdateRequest;
import com.ethara.taskmanager.dto.TaskRequest;
import com.ethara.taskmanager.dto.TaskResponse;
import com.ethara.taskmanager.exception.BadRequestException;
import com.ethara.taskmanager.exception.ResourceNotFoundException;
import com.ethara.taskmanager.exception.UnauthorizedException;
import com.ethara.taskmanager.model.Project;
import com.ethara.taskmanager.model.Task;
import com.ethara.taskmanager.model.User;
import com.ethara.taskmanager.repository.ProjectMemberRepository;
import com.ethara.taskmanager.repository.ProjectRepository;
import com.ethara.taskmanager.repository.TaskRepository;
import com.ethara.taskmanager.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMemberRepository projectMemberRepository;

    public TaskService(TaskRepository taskRepository, ProjectRepository projectRepository,
                       UserRepository userRepository, ProjectMemberRepository projectMemberRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.projectMemberRepository = projectMemberRepository;
    }

    @Transactional
    public TaskResponse createTask(TaskRequest request, String userEmail) {
        User creator = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + request.getProjectId()));
        boolean isMember = projectMemberRepository.existsByUserAndProject(creator, project);
        boolean isCreator = project.getCreatedBy().getId().equals(creator.getId());
        if (!isMember && !isCreator) {
            throw new UnauthorizedException("You are not a member of this project");
        }
        Task task = Task.builder().title(request.getTitle()).description(request.getDescription())
                .status(Task.TaskStatus.TODO).project(project).createdBy(creator).build();
        if (request.getDueDate() != null && !request.getDueDate().isBlank()) {
            task.setDueDate(LocalDate.parse(request.getDueDate()));
        }
        if (request.getAssignedToId() != null) {
            User assignedTo = userRepository.findById(request.getAssignedToId())
                    .orElseThrow(() -> new ResourceNotFoundException("Assigned user not found"));
            boolean assigneeMember = projectMemberRepository.existsByUserAndProject(assignedTo, project);
            boolean assigneeCreator = project.getCreatedBy().getId().equals(assignedTo.getId());
            if (!assigneeMember && !assigneeCreator) {
                throw new BadRequestException("Assigned user is not a member of this project");
            }
            task.setAssignedTo(assignedTo);
        }
        task = taskRepository.save(task);
        return mapToResponse(task);
    }

    public List<TaskResponse> getTasksByProject(Long projectId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
        boolean isMember = projectMemberRepository.existsByUserAndProject(user, project);
        boolean isCreator = project.getCreatedBy().getId().equals(user.getId());
        if (!isMember && !isCreator) {
            throw new UnauthorizedException("You are not a member of this project");
        }
        return taskRepository.findByProject(project).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional
    public TaskResponse updateTaskStatus(Long taskId, StatusUpdateRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));
        boolean isMember = projectMemberRepository.existsByUserAndProject(user, task.getProject());
        boolean isCreator = task.getProject().getCreatedBy().getId().equals(user.getId());
        if (!isMember && !isCreator) {
            throw new UnauthorizedException("You are not a member of this project");
        }
        Task.TaskStatus newStatus;
        try {
            newStatus = Task.TaskStatus.valueOf(request.getStatus().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid status. Must be: TODO, IN_PROGRESS, IN_REVIEW, DONE");
        }
        task.setStatus(newStatus);
        task = taskRepository.save(task);
        return mapToResponse(task);
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
