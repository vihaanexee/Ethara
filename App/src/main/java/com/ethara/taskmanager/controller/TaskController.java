package com.ethara.taskmanager.controller;

import com.ethara.taskmanager.dto.StatusUpdateRequest;
import com.ethara.taskmanager.dto.TaskRequest;
import com.ethara.taskmanager.dto.TaskResponse;
import com.ethara.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest request,
                                                    Authentication authentication) {
        return ResponseEntity.ok(taskService.createTask(request, authentication.getName()));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<TaskResponse>> getTasksByProject(@PathVariable Long projectId,
                                                                 Authentication authentication) {
        return ResponseEntity.ok(taskService.getTasksByProject(projectId, authentication.getName()));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<TaskResponse> updateStatus(@PathVariable Long id,
                                                      @Valid @RequestBody StatusUpdateRequest request,
                                                      Authentication authentication) {
        return ResponseEntity.ok(taskService.updateTaskStatus(id, request, authentication.getName()));
    }
}
