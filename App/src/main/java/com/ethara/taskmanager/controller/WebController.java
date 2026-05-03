package com.ethara.taskmanager.controller;

import com.ethara.taskmanager.dto.DashboardSummary;
import com.ethara.taskmanager.dto.ProjectResponse;
import com.ethara.taskmanager.dto.TaskResponse;
import com.ethara.taskmanager.model.Project;
import com.ethara.taskmanager.model.ProjectMember;
import com.ethara.taskmanager.model.Task;
import com.ethara.taskmanager.model.User;
import com.ethara.taskmanager.repository.UserRepository;
import com.ethara.taskmanager.service.AdminService;
import com.ethara.taskmanager.service.DashboardService;
import com.ethara.taskmanager.service.ProjectService;
import com.ethara.taskmanager.service.TaskService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.time.LocalDate;
import java.util.List;

@Controller
public class WebController {

    private final ProjectService projectService;
    private final TaskService taskService;
    private final DashboardService dashboardService;
    private final AdminService adminService;
    private final UserRepository userRepository;

    public WebController(ProjectService projectService, TaskService taskService,
                         DashboardService dashboardService, AdminService adminService,
                         UserRepository userRepository) {
        this.projectService = projectService;
        this.taskService = taskService;
        this.dashboardService = dashboardService;
        this.adminService = adminService;
        this.userRepository = userRepository;
    }

    @GetMapping("/")
    public String home() { return "redirect:/login"; }

    @GetMapping("/login")
    public String loginPage() { return "login"; }

    @GetMapping("/signup")
    public String signupPage() { return "signup"; }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        String email = authentication.getName();
        DashboardSummary summary = dashboardService.getSummary(email);
        List<TaskResponse> myTasks = dashboardService.getMyTasks(email);
        List<TaskResponse> overdueTasks = dashboardService.getOverdueTasks(email);
        List<ProjectResponse> projects = projectService.getUserProjects(email);
        User user = userRepository.findByEmail(email).orElse(null);
        model.addAttribute("userName", user != null ? user.getName() : email);
        model.addAttribute("userEmail", email);
        model.addAttribute("summary", summary);
        model.addAttribute("myTasks", myTasks);
        model.addAttribute("overdueTasks", overdueTasks);
        model.addAttribute("projects", projects);
        return "dashboard";
    }

    @GetMapping("/projects")
    public String projectsPage(Authentication authentication, Model model) {
        String email = authentication.getName();
        List<ProjectResponse> projects = projectService.getUserProjects(email);
        User user = userRepository.findByEmail(email).orElse(null);
        model.addAttribute("userName", user != null ? user.getName() : email);
        model.addAttribute("projects", projects);
        return "projects";
    }

    @GetMapping("/projects/{id}/tasks")
    public String taskBoard(@PathVariable Long id, Authentication auth, Model model) {
        String email = auth.getName();
        ProjectResponse project = projectService.getProjectById(id, email);
        List<TaskResponse> tasks = taskService.getTasksByProject(id, email);
        List<ProjectMember> members = projectService.getProjectMembers(id);
        User user = userRepository.findByEmail(email).orElse(null);
        model.addAttribute("userName", user != null ? user.getName() : email);
        model.addAttribute("project", project);
        model.addAttribute("tasks", tasks);
        model.addAttribute("members", members);
        model.addAttribute("projectId", id);
        model.addAttribute("todoTasks", tasks.stream().filter(t -> "TODO".equals(t.getStatus())).toList());
        model.addAttribute("inProgressTasks", tasks.stream().filter(t -> "IN_PROGRESS".equals(t.getStatus())).toList());
        model.addAttribute("inReviewTasks", tasks.stream().filter(t -> "IN_REVIEW".equals(t.getStatus())).toList());
        model.addAttribute("doneTasks", tasks.stream().filter(t -> "DONE".equals(t.getStatus())).toList());
        return "taskboard";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminPanel(Authentication authentication, Model model) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElse(null);
        model.addAttribute("userName", user != null ? user.getName() : email);

        List<User> allUsers = adminService.getAllUsers();
        List<Project> allProjects = adminService.getAllProjects();
        List<Task> allTasks = adminService.getAllTasks();

        long totalUsers = allUsers.size();
        long adminCount = allUsers.stream().filter(u -> u.getRole() == User.Role.ADMIN).count();
        long totalProjects = allProjects.size();
        long totalTasks = allTasks.size();
        long completedTasks = allTasks.stream().filter(t -> t.getStatus() == Task.TaskStatus.DONE).count();
        long overdueTasks = allTasks.stream().filter(t ->
                t.getDueDate() != null && t.getDueDate().isBefore(LocalDate.now())
                && t.getStatus() != Task.TaskStatus.DONE).count();

        model.addAttribute("allUsers", allUsers);
        model.addAttribute("allProjects", allProjects);
        model.addAttribute("allTasks", allTasks);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("adminCount", adminCount);
        model.addAttribute("totalProjects", totalProjects);
        model.addAttribute("totalTasks", totalTasks);
        model.addAttribute("completedTasks", completedTasks);
        model.addAttribute("overdueTasks", overdueTasks);
        return "admin";
    }
}
