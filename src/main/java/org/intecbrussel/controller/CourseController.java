package org.intecbrussel.controller;

import lombok.RequiredArgsConstructor;
import org.intecbrussel.dto.CourseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.intecbrussel.model.Course;
import org.intecbrussel.model.User;
import org.intecbrussel.dto.CourseRequest;
import org.intecbrussel.service.CourseService;
import org.intecbrussel.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final UserService userService;

    // ================= LIST =================
    @GetMapping
    public ResponseEntity<List<CourseResponse>> listCourses() {
        return ResponseEntity.ok(
                courseService.listAll()
                        .stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseResponse> getCourse(@PathVariable Long id) {
        return ResponseEntity.ok(
                toResponse(courseService.getById(id))
        );
    }

    // ================= CREATE =================
    @PostMapping
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<CourseResponse> createCourse(@RequestBody CourseRequest request) {
        User currentUser = getCurrentUser();
        Course course = courseService.createCourse(
                request.getTitle(),
                request.getDescription(),
                currentUser
        );
        return ResponseEntity.ok(toResponse(course));
    }

    // ================= UPDATE =================
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<CourseResponse> updateCourse(
            @PathVariable Long id,
            @RequestBody CourseRequest request) {

        Course course = courseService.updateCourse(
                id,
                request.getTitle(),
                request.getDescription(),
                getCurrentUser()
        );
        return ResponseEntity.ok(toResponse(course));
    }

    // ================= DELETE =================
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.ok("Course deleted successfully");
    }

    // ================= HELPERS =================
    private User getCurrentUser() {
        String username = (String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return userService.findByUsername(username);
    }

    private CourseResponse toResponse(Course course) {
        CourseResponse response = new CourseResponse();
        response.setId(course.getId());
        response.setTitle(course.getTitle());
        response.setDescription(course.getDescription());
        response.setInstructorUsername(course.getInstructor().getUsername());
        return response;
    }
}
