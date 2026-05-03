package com.ethara.taskmanager.controller;

import com.ethara.taskmanager.dto.AddMemberRequest;
import com.ethara.taskmanager.dto.ProjectRequest;
import com.ethara.taskmanager.dto.ProjectResponse;
import com.ethara.taskmanager.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody ProjectRequest request,
                                                          Authentication authentication) {
        return ResponseEntity.ok(projectService.createProject(request, authentication.getName()));
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getProjects(Authentication authentication) {
        return ResponseEntity.ok(projectService.getUserProjects(authentication.getName()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProject(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(projectService.getProjectById(id, authentication.getName()));
    }

    @PostMapping("/{id}/add-member")
    public ResponseEntity<Map<String, String>> addMember(@PathVariable Long id,
                                                          @Valid @RequestBody AddMemberRequest request,
                                                          Authentication authentication) {
        String message = projectService.addMember(id, request, authentication.getName());
        return ResponseEntity.ok(Map.of("message", message));
    }
}
