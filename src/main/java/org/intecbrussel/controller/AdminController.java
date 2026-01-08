package org.intecbrussel.controller;

import lombok.RequiredArgsConstructor;
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
@PreAuthorize("hasRole('ADMIN')") // alle endpoints in deze controller = admin-only
public class AdminController {

    private final UserService userService;

    // ---------------- Get all users ----------------
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // ---------------- Change user role ----------------
    @PutMapping("/users/{id}/role")
    public ResponseEntity<User> changeUserRole(
            @PathVariable Long id,
            @RequestParam Role role) {

        User updated = userService.changeRole(id, role);
        return ResponseEntity.ok(updated);
    }

    // ---------------- Delete user ----------------
    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }
}
