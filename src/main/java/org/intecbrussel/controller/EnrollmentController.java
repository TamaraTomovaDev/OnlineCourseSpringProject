package org.intecbrussel.controller;

import lombok.RequiredArgsConstructor;
import org.intecbrussel.dto.EnrollmentResponse;
import org.intecbrussel.model.Enrollment;
import org.intecbrussel.model.User;
import org.intecbrussel.service.EnrollmentService;
import org.intecbrussel.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final UserService userService;

    // -------- STUDENT --------
    @PostMapping("/courses/{courseId}/enroll")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<EnrollmentResponse> enrollSelf(@PathVariable Long courseId) {
        Enrollment enrollment = enrollmentService.enrollSelf(courseId, getCurrentUser());
        return ResponseEntity.ok(toResponse(enrollment));
    }

    @GetMapping("/enrollments/me")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<EnrollmentResponse>> myEnrollments() {
        return ResponseEntity.ok(
                enrollmentService.getStudentEnrollments(getCurrentUser())
                        .stream().map(this::toResponse).toList()
        );
    }

    // -------- INSTRUCTOR --------
    @GetMapping("/instructor/enrollments")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<List<EnrollmentResponse>> instructorEnrollments() {
        return ResponseEntity.ok(
                enrollmentService.getInstructorEnrollments(getCurrentUser())
                        .stream().map(this::toResponse).toList()
        );
    }

    // -------- ADMIN --------
    @PostMapping("/admin/courses/{courseId}/enroll/{studentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EnrollmentResponse> enrollAsAdmin(
            @PathVariable Long courseId,
            @PathVariable Long studentId) {

        return ResponseEntity.ok(
                toResponse(enrollmentService.enrollAsAdmin(courseId, studentId))
        );
    }

    @GetMapping("/admin/enrollments")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<EnrollmentResponse>> allEnrollments() {
        return ResponseEntity.ok(
                enrollmentService.getAllEnrollments()
                        .stream().map(this::toResponse).toList()
        );
    }

    // -------- SHARED (DELETE) --------
    @DeleteMapping("/enrollments/{id}")
    @PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
    public ResponseEntity<String> cancelEnrollment(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        enrollmentService.cancelEnrollment(id, currentUser); // service layer checkt of student alleen eigen enrollment kan cancelen
        return ResponseEntity.ok("Enrollment canceled successfully");
    }

    // -------- HELPERS --------
    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();
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
