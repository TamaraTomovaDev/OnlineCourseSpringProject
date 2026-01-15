package org.intecbrussel.service;

import org.intecbrussel.exception.ResourceNotFoundException;
import org.intecbrussel.exception.UnauthorizedActionException;
import org.intecbrussel.model.Role;
import org.intecbrussel.model.User;
import org.intecbrussel.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User admin;
    private User student;

    @BeforeEach
    void setUp() {
        admin = new User();
        admin.setId(1L);
        admin.setUsername("admin");
        admin.setRole(Role.ADMIN);

        student = new User();
        student.setId(2L);
        student.setUsername("student");
        student.setRole(Role.STUDENT);
    }

    // ================= getAllUsers =================
    @Test
    void getAllUsers_ReturnsList() {
        when(userRepository.findAll()).thenReturn(java.util.List.of(admin, student));

        var users = userService.getAllUsers();

        assertEquals(2, users.size());
        verify(userRepository).findAll();
    }

    // ================= changeRole =================
    @Test
    void changeRole_UpdatesRole() {
        when(userRepository.findById(student.getId())).thenReturn(Optional.of(student));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var updated = userService.changeRole(student.getId(), Role.INSTRUCTOR);

        assertEquals(Role.INSTRUCTOR, updated.getRole());
        verify(userRepository).save(student);
    }

    @Test
    void changeRole_UserNotFound_Throws() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.changeRole(999L, Role.INSTRUCTOR));
    }

    // ================= deleteUser =================
    @Test
    void deleteUser_AdminDeletesOtherUser_Success() {
        when(userRepository.findById(student.getId())).thenReturn(Optional.of(student));

        userService.deleteUser(student.getId(), admin);

        verify(userRepository).delete(student);
    }

    @Test
    void deleteUser_AdminDeletesSelf_ThrowsUnauthorized() {
        // gebruik lenient zodat deze stubbing niet tot UnnecessaryStubbingException leidt
        lenient().when(userRepository.findById(admin.getId())).thenReturn(Optional.of(admin));

        assertThrows(UnauthorizedActionException.class, () -> userService.deleteUser(admin.getId(), admin));

        // delete mag niet aangeroepen worden
        verify(userRepository, never()).delete(any());
    }

    @Test
    void deleteUser_UserNotFound_Throws() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(999L, admin));
    }

    // ================= findByUsername =================
    @Test
    void findByUsername_ReturnsUser() {
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(admin));

        var found = userService.findByUsername("admin");

        assertEquals("admin", found.getUsername());
    }

    @Test
    void findByUsername_NotFound_Throws() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.findByUsername("unknown"));
    }
}
