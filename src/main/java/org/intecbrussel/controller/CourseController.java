package org.intecbrussel.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.intecbrussel.model.Course;
import org.intecbrussel.model.User;
import org.intecbrussel.dto.CourseRequest;
import org.intecbrussel.service.CourseService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    // ---------------- List courses (public) ----------------
    @GetMapping
    public ResponseEntity<List<Course>> listCourses() {
        return ResponseEntity.ok(courseService.listCourses());
    }

    // ---------------- Create course ----------------
    @PostMapping
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<Course> createCourse(@RequestBody CourseRequest request) {
        User currentUser = getCurrentUser();
        Course course = new Course();
        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        Course created = courseService.createCourse(course, currentUser);
        return ResponseEntity.ok(created);
    }

    // ---------------- Update course ----------------
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<Course> updateCourse(@PathVariable Long id,
                                               @RequestBody CourseRequest request) {
        User currentUser = getCurrentUser();
        Course updated = new Course();
        updated.setTitle(request.getTitle());
        updated.setDescription(request.getDescription());
        Course course = courseService.updateCourse(id, updated, currentUser);
        return ResponseEntity.ok(course);
    }

    // ---------------- Delete course ----------------
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteCourse(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        courseService.deleteCourse(id, currentUser);
        return ResponseEntity.ok("Course deleted successfully");
    }

    // ---------------- Helper: get current authenticated user ----------------
    private User getCurrentUser() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // TODO: haal User entity op uit DB via UserRepository
        // Je kan hier een UserService methode toevoegen: findByUsername(username)
        return new User(); // tijdelijke placeholder, vervang later
    }
}
