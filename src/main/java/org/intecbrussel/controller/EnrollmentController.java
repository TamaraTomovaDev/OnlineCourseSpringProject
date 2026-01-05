package org.intecbrussel.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.intecbrussel.model.Enrollment;
import org.intecbrussel.model.User;
import org.intecbrussel.service.EnrollmentService;
import org.intecbrussel.service.UserService;
import org.intecbrussel.dto.EnrollmentResponse;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final UserService userService;

    // ---------------- Enroll student ----------------
    @PostMapping("/api/courses/{id}/enroll")
    @PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
    public ResponseEntity<EnrollmentResponse> enrollStudent(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        Enrollment enrollment = enrollmentService.enrollStudent(id, currentUser);
        return ResponseEntity.ok(toResponse(enrollment));
    }

    // ---------------- Get enrollments (STUDENT self) ----------------
    @GetMapping("/api/enrollments/me")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<EnrollmentResponse>> myEnrollments() {
        User currentUser = getCurrentUser();
        List<EnrollmentResponse> responseList = enrollmentService.listEnrollments(currentUser)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseList);
    }

    // ---------------- Get enrollments (INSTRUCTOR own courses) ----------------
    @GetMapping("/api/instructor/enrollments")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<List<EnrollmentResponse>> instructorEnrollments() {
        User currentUser = getCurrentUser();
        List<EnrollmentResponse> responseList = enrollmentService.listEnrollments(currentUser)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseList);
    }

    // ---------------- Get enrollments (ADMIN sees all) ----------------
    @GetMapping("/api/admin/enrollments")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<EnrollmentResponse>> adminEnrollments() {
        User currentUser = getCurrentUser();
        List<EnrollmentResponse> responseList = enrollmentService.listEnrollments(currentUser)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseList);
    }

    // ---------------- Cancel enrollment ----------------
    @DeleteMapping("/api/enrollments/{id}")
    @PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
    public ResponseEntity<String> cancelEnrollment(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        enrollmentService.cancelEnrollment(id, currentUser);
        return ResponseEntity.ok("Enrollment canceled successfully");
    }

    // ---------------- Helper: get current authenticated user ----------------
    private User getCurrentUser() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.findByUsername(username);
    }

    // ---------------- Helper: convert Enrollment → EnrollmentResponse ----------------
    private EnrollmentResponse toResponse(Enrollment enrollment) {
        EnrollmentResponse response = new EnrollmentResponse();
        response.setId(enrollment.getId());
        response.setStudentUsername(enrollment.getStudent().getUsername());
        response.setCourseTitle(enrollment.getCourse().getTitle());
        response.setEnrollmentDate(enrollment.getEnrollmentDate());
        return response;
    }
}
