package com.ethara.taskmanager.controller;

import com.ethara.taskmanager.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<Map<String, String>> changeUserRole(@PathVariable Long id,
                                                               @RequestBody Map<String, String> body) {
        adminService.changeUserRole(id, body.get("role"));
        return ResponseEntity.ok(Map.of("message", "Role updated successfully"));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
    }
}
