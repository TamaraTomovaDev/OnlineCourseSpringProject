package org.intecbrussel.service;

import lombok.RequiredArgsConstructor;
import org.intecbrussel.dto.AuthResponse;
import org.intecbrussel.dto.LoginRequest;
import org.intecbrussel.dto.RegisterRequest;
import org.intecbrussel.exception.DuplicateResourceException;
import org.intecbrussel.exception.InvalidCredentialsException;
import org.intecbrussel.model.Role;
import org.intecbrussel.model.User;
import org.intecbrussel.repository.UserRepository;
import org.intecbrussel.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String username = request.getUsername().trim();
        String email = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByUsername(username)) {
            throw new DuplicateResourceException("Username already exists");
        }
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Email already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.STUDENT);

        User saved = userRepository.save(user);

        String token = jwtUtil.generateToken(saved.getUsername(), saved.getRole().name());

        return new AuthResponse(token, saved.getUsername(), saved.getEmail(), saved.getRole());
    }

    public AuthResponse login(LoginRequest request) {
        String username = request.getUsername().trim();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());

        return new AuthResponse(token, user.getUsername(), user.getEmail(), user.getRole());
    }
}
