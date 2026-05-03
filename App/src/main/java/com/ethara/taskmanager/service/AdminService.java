package com.ethara.taskmanager.service;

import com.ethara.taskmanager.exception.BadRequestException;
import com.ethara.taskmanager.exception.ResourceNotFoundException;
import com.ethara.taskmanager.model.Project;
import com.ethara.taskmanager.model.Task;
import com.ethara.taskmanager.model.User;
import com.ethara.taskmanager.repository.ProjectRepository;
import com.ethara.taskmanager.repository.TaskRepository;
import com.ethara.taskmanager.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;

    public AdminService(UserRepository userRepository, ProjectRepository projectRepository,
                        TaskRepository taskRepository) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @Transactional
    public void changeUserRole(Long userId, String newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        try {
            user.setRole(User.Role.valueOf(newRole.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid role. Must be ADMIN or MEMBER");
        }
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        userRepository.deleteById(userId);
    }
}
