package org.intecbrussel.controller;

import lombok.RequiredArgsConstructor;
import org.intecbrussel.dto.UserResponse;
import org.intecbrussel.model.Role;
import org.intecbrussel.model.User;
import org.intecbrussel.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    public record ChangeRoleRequest(@jakarta.validation.constraints.NotNull Role role) {}

    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers()
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(users);
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<UserResponse> changeUserRole(
            @PathVariable Long id,
            @jakarta.validation.Valid @RequestBody ChangeRoleRequest request) {

        return ResponseEntity.ok(toResponse(userService.changeRole(id, request.role())));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        // current admin ophalen
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentAdmin = userService.findByUsername(username);

        userService.deleteUser(id, currentAdmin);
        return ResponseEntity.noContent().build(); // 204
    }

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
