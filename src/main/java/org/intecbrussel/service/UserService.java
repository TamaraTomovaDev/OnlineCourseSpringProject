package org.intecbrussel.service;

import lombok.RequiredArgsConstructor;
import org.intecbrussel.exception.UnauthorizedActionException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.intecbrussel.model.Role;
import org.intecbrussel.model.User;
import org.intecbrussel.repository.UserRepository;
import org.intecbrussel.exception.ResourceNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User changeRole(Long userId, Role newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        user.setRole(newRole);
        return userRepository.save(user);
    }

    public void deleteUser(Long userId, User currentAdmin) {
        if (currentAdmin != null && currentAdmin.getId().equals(userId)) {
            throw new UnauthorizedActionException("Admin cannot delete نفسه (self)");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        userRepository.delete(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }
}

