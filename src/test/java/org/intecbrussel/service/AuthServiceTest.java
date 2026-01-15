package org.intecbrussel.service;

import org.intecbrussel.dto.AuthResponse;
import org.intecbrussel.dto.LoginRequest;
import org.intecbrussel.dto.RegisterRequest;
import org.intecbrussel.exception.DuplicateResourceException;
import org.intecbrussel.exception.InvalidCredentialsException;
import org.intecbrussel.model.Role;
import org.intecbrussel.model.User;
import org.intecbrussel.repository.UserRepository;
import org.intecbrussel.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private User student;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        student = new User();
        student.setId(1L);
        student.setUsername("tamara");
        student.setEmail("tamara@test.com");
        student.setPassword("encodedPassword");
        student.setRole(Role.STUDENT);

        registerRequest = new RegisterRequest();
        registerRequest.setUsername("tamara");
        registerRequest.setEmail("tamara@test.com");
        registerRequest.setPassword("password123");

        loginRequest = new LoginRequest();
        loginRequest.setUsername("tamara");
        loginRequest.setPassword("password123");

        // Lenient stubs voor algemene gevallen
        lenient().when(userRepository.existsByUsername(anyString())).thenReturn(false);
        lenient().when(userRepository.existsByEmail(anyString())).thenReturn(false);
        lenient().when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        lenient().when(userRepository.save(any(User.class))).thenReturn(student);
        lenient().when(jwtUtil.generateToken(anyString(), anyString())).thenReturn("fake-jwt-token");
    }

    @Test
    void register_Success() {
        AuthResponse response = authService.register(registerRequest);
        assertNotNull(response);
        assertEquals("tamara", response.getUsername());
        assertEquals("tamara@test.com", response.getEmail());
        assertEquals(Role.STUDENT, response.getRole());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_DuplicateUsername_ThrowsException() {
        when(userRepository.existsByUsername("tamara")).thenReturn(true);
        DuplicateResourceException ex = assertThrows(DuplicateResourceException.class,
                () -> authService.register(registerRequest));
        assertEquals("Username already exists", ex.getMessage());
    }

    @Test
    void register_DuplicateEmail_ThrowsException() {
        when(userRepository.existsByEmail("tamara@test.com")).thenReturn(true);
        DuplicateResourceException ex = assertThrows(DuplicateResourceException.class,
                () -> authService.register(registerRequest));
        assertEquals("Email already exists", ex.getMessage());
    }

    @Test
    void login_Success() {
        when(userRepository.findByUsername("tamara")).thenReturn(Optional.of(student));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);

        AuthResponse response = authService.login(loginRequest);
        assertNotNull(response);
        assertEquals("tamara", response.getUsername());
        assertEquals("tamara@test.com", response.getEmail());
        assertEquals(Role.STUDENT, response.getRole());
    }

    @Test
    void login_InvalidUsername_ThrowsException() {
        when(userRepository.findByUsername("tamara")).thenReturn(Optional.empty());
        InvalidCredentialsException ex = assertThrows(InvalidCredentialsException.class,
                () -> authService.login(loginRequest));
        assertEquals("Invalid username or password", ex.getMessage());
    }

    @Test
    void login_InvalidPassword_ThrowsException() {
        when(userRepository.findByUsername("tamara")).thenReturn(Optional.of(student));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(false);

        InvalidCredentialsException ex = assertThrows(InvalidCredentialsException.class,
                () -> authService.login(loginRequest));
        assertEquals("Invalid username or password", ex.getMessage());
    }
}
