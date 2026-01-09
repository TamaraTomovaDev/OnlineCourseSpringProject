package org.intecbrussel.dto;

import lombok.Data;
import org.intecbrussel.model.Role;

import java.time.LocalDateTime;

@Data
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private Role role;
    private LocalDateTime createdAt;
}
