package org.intecbrussel.controller;

import lombok.RequiredArgsConstructor;
import org.intecbrussel.dto.CourseRequest;
import org.intecbrussel.dto.CourseResponse;
import org.intecbrussel.model.Course;
import org.intecbrussel.model.User;
import org.intecbrussel.service.CourseService;
import org.intecbrussel.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final UserService userService;

    // ---------------- LIST ALL COURSES ----------------
    @GetMapping
    public ResponseEntity<List<CourseResponse>> getAllCourses() {
        List<Course> courses = courseService.listAll();
        List<CourseResponse> responses = courses.stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    // ---------------- GET COURSE BY ID ----------------
    @GetMapping("/{id}")
    public ResponseEntity<CourseResponse> getCourse(@PathVariable Long id) {
        Course course = courseService.getById(id);
        return ResponseEntity.ok(toResponse(course));
    }

    // ---------------- CREATE COURSE ----------------
    @PostMapping
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<CourseResponse> createCourse(@RequestBody CourseRequest request) {
        User currentUser = getCurrentUser();
        Course course = courseService.createCourse(request.getTitle(), request.getDescription(), currentUser);
        return ResponseEntity.ok(toResponse(course));
    }

    // ---------------- UPDATE COURSE ----------------
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<CourseResponse> updateCourse(
            @PathVariable Long id,
            @RequestBody CourseRequest request) {

        User currentUser = getCurrentUser();
        Course course = courseService.updateCourse(id, request.getTitle(), request.getDescription(), currentUser);
        return ResponseEntity.ok(toResponse(course));
    }

    // ---------------- DELETE COURSE ----------------
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // alleen ADMIN
    public ResponseEntity<String> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.ok("Course deleted successfully");
    }

    // ---------------- HELPER ----------------
    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userService.findByUsername(username);
    }

    private CourseResponse toResponse(Course course) {
        CourseResponse response = new CourseResponse();
        response.setId(course.getId());
        response.setTitle(course.getTitle());
        response.setDescription(course.getDescription());
        response.setInstructorUsername(course.getInstructor().getUsername());
        response.setCreatedAt(course.getCreatedAt());
        response.setUpdatedAt(course.getUpdatedAt());
        return response;
    }
}
