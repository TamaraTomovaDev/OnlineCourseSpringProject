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

@RestController
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final UserService userService;

    // ================= ENROLL =================

    // STUDENT self-enroll
    @PostMapping("/api/courses/{courseId}/enroll")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<EnrollmentResponse> enrollSelf(@PathVariable Long courseId) {
        User currentUser = getCurrentUser();
        Enrollment enrollment = enrollmentService.enrollSelf(courseId, currentUser);
        return ResponseEntity.ok(toResponse(enrollment));
    }

    // ADMIN enrolls student
    @PostMapping("/api/admin/courses/{courseId}/enroll/{studentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EnrollmentResponse> enrollAsAdmin(
            @PathVariable Long courseId,
            @PathVariable Long studentId) {

        Enrollment enrollment = enrollmentService.enrollAsAdmin(courseId, studentId);
        return ResponseEntity.ok(toResponse(enrollment));
    }

    // ================= LIST =================

    @GetMapping("/api/enrollments/me")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<EnrollmentResponse>> myEnrollments() {
        User currentUser = getCurrentUser();
        return ResponseEntity.ok(
                enrollmentService.getStudentEnrollments(currentUser)
                        .stream().map(this::toResponse).toList()
        );
    }

    @GetMapping("/api/instructor/enrollments")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<List<EnrollmentResponse>> instructorEnrollments() {
        User currentUser = getCurrentUser();
        return ResponseEntity.ok(
                enrollmentService.getInstructorEnrollments(currentUser)
                        .stream().map(this::toResponse).toList()
        );
    }

    @GetMapping("/api/admin/enrollments")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<EnrollmentResponse>> adminEnrollments() {
        return ResponseEntity.ok(
                enrollmentService.getAllEnrollments()
                        .stream().map(this::toResponse).toList()
        );
    }

    // ================= CANCEL =================

    @DeleteMapping("/api/enrollments/{id}")
    @PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
    public ResponseEntity<String> cancelEnrollment(@PathVariable Long id) {
        enrollmentService.cancelEnrollment(id, getCurrentUser());
        return ResponseEntity.ok("Enrollment canceled successfully");
    }

    // ================= HELPERS =================

    private User getCurrentUser() {
        String username = (String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return userService.findByUsername(username);
    }

    private EnrollmentResponse toResponse(Enrollment enrollment) {
        EnrollmentResponse response = new EnrollmentResponse();
        response.setId(enrollment.getId());
        response.setStudentUsername(enrollment.getStudent().getUsername());
        response.setCourseTitle(enrollment.getCourse().getTitle());
        response.setEnrollmentDate(enrollment.getEnrollmentDate());
        return response;
    }
}

