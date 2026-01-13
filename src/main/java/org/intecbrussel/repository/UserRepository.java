package org.intecbrussel.repository;

import org.intecbrussel.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email); // handig als je login met email wil ondersteunen

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
