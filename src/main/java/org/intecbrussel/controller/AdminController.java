package org.intecbrussel.controller;

import lombok.RequiredArgsConstructor;
import org.intecbrussel.dto.UserResponse;
import org.intecbrussel.model.Role;
import org.intecbrussel.model.User;
import org.intecbrussel.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") // alles in deze controller = admin-only
public class AdminController {
    public record ChangeRoleRequest(Role role) {}
    private final UserService userService;

    // ---------------- GET ALL USERS ----------------
    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers()
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(users);
    }

    // ---------------- CHANGE USER ROLE ----------------
    @PutMapping("/users/{id}/role")
    public ResponseEntity<UserResponse> changeUserRole(
            @PathVariable Long id,
            @RequestBody ChangeRoleRequest request) {

        return ResponseEntity.ok(
                toResponse(userService.changeRole(id, request.role()))
        );
    }

    // ---------------- DELETE USER ----------------
    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }

    // ---------------- DTO MAPPER ----------------
    private UserResponse toResponse(User user) {
        UserResponse dto = new UserResponse();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}