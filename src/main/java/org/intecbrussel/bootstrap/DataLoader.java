package org.intecbrussel.bootstrap;

import lombok.RequiredArgsConstructor;
import org.intecbrussel.model.*;
import org.intecbrussel.repository.CourseRepository;
import org.intecbrussel.repository.EnrollmentRepository;
import org.intecbrussel.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {

        // Vereiste: seed enkel als DB leeg is
        if (userRepository.count() > 0) {
            return;
        }

        // USERS
        User admin = new User(null, "admin", "admin@test.com",
                passwordEncoder.encode("admin123"), Role.ADMIN);

        User instructor1 = new User(null, "hilal", "hilal@test.com",
                passwordEncoder.encode("inst123"), Role.INSTRUCTOR);

        User instructor2 = new User(null, "teodora", "teodora@test.com",
                passwordEncoder.encode("inst123"), Role.INSTRUCTOR);

        User student1 = new User(null, "tamara", "tamara@test.com",
                passwordEncoder.encode("stud123"), Role.STUDENT);

        User student2 = new User(null, "eva", "eva@test.com",
                passwordEncoder.encode("stud123"), Role.STUDENT);

        User student3 = new User(null, "vika", "vika@test.com",
                passwordEncoder.encode("stud123"), Role.STUDENT);

        userRepository.saveAll(List.of(admin, instructor1, instructor2, student1, student2, student3));

        // COURSES
        Course course1 = new Course(null, "Java Fundamentals", "Intro to Java", instructor1);
        Course course2 = new Course(null, "Spring Boot", "Build secure REST APIs", instructor1);
        Course course3 = new Course(null, "Nederlands", "Nederlands oefenen", instructor2);

        courseRepository.saveAll(List.of(course1, course2, course3));

        // ENROLLMENTS
        // (Als je @PrePersist gebruikt in Enrollment, mag enrollmentDate null zijn.)
        LocalDateTime now = LocalDateTime.now();

        Enrollment e1 = new Enrollment(null, student1, course1, now);
        Enrollment e2 = new Enrollment(null, student1, course2, now);
        Enrollment e3 = new Enrollment(null, student2, course1, now);
        Enrollment e4 = new Enrollment(null, student3, course3, now);

        enrollmentRepository.saveAll(List.of(e1, e2, e3, e4));

        System.out.println("✅ Database seeded successfully");
    }
}
