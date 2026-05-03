package com.ethara.taskmanager.service;

import com.ethara.taskmanager.dto.AddMemberRequest;
import com.ethara.taskmanager.dto.ProjectRequest;
import com.ethara.taskmanager.dto.ProjectResponse;
import com.ethara.taskmanager.exception.*;
import com.ethara.taskmanager.model.Project;
import com.ethara.taskmanager.model.ProjectMember;
import com.ethara.taskmanager.model.User;
import com.ethara.taskmanager.repository.ProjectMemberRepository;
import com.ethara.taskmanager.repository.ProjectRepository;
import com.ethara.taskmanager.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;

    public ProjectService(ProjectRepository projectRepository, ProjectMemberRepository projectMemberRepository,
                          UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ProjectResponse createProject(ProjectRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Project project = Project.builder().name(request.getName()).description(request.getDescription())
                .createdBy(user).build();
        project = projectRepository.save(project);
        ProjectMember member = ProjectMember.builder().user(user).project(project)
                .role(ProjectMember.MemberRole.ADMIN).build();
        projectMemberRepository.save(member);
        return mapToResponse(project);
    }

    public List<ProjectResponse> getUserProjects(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return projectRepository.findAllByUser(user).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public ProjectResponse getProjectById(Long projectId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
        boolean isCreator = project.getCreatedBy().getId().equals(user.getId());
        boolean isMember = projectMemberRepository.existsByUserAndProject(user, project);
        if (!isCreator && !isMember) {
            throw new UnauthorizedException("You are not a member of this project");
        }
        return mapToResponse(project);
    }

    @Transactional
    public String addMember(Long projectId, AddMemberRequest request, String userEmail) {
        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
        boolean isCreator = project.getCreatedBy().getId().equals(currentUser.getId());
        boolean isAdmin = projectMemberRepository.findByUserAndProject(currentUser, project)
                .map(pm -> pm.getRole() == ProjectMember.MemberRole.ADMIN).orElse(false);
        if (!isCreator && !isAdmin) {
            throw new UnauthorizedException("Only project admins can add members");
        }
        User newMember = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + request.getEmail()));
        if (projectMemberRepository.existsByUserAndProject(newMember, project)) {
            throw new DuplicateResourceException("User is already a member of this project");
        }
        ProjectMember.MemberRole memberRole;
        try {
            memberRole = ProjectMember.MemberRole.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid role. Must be ADMIN or MEMBER");
        }
        ProjectMember projectMember = ProjectMember.builder().user(newMember).project(project)
                .role(memberRole).build();
        projectMemberRepository.save(projectMember);
        return "Member added successfully";
    }

    public List<ProjectMember> getProjectMembers(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
        return projectMemberRepository.findByProject(project);
    }

    private ProjectResponse mapToResponse(Project project) {
        return ProjectResponse.builder().id(project.getId()).name(project.getName())
                .description(project.getDescription()).createdByName(project.getCreatedBy().getName())
                .createdByEmail(project.getCreatedBy().getEmail()).createdAt(project.getCreatedAt()).build();
    }
}
