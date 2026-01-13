package org.intecbrussel.controller;

import lombok.RequiredArgsConstructor;
import org.intecbrussel.dto.EnrollmentResponse;
import org.intecbrussel.exception.UnauthorizedActionException;
import org.intecbrussel.model.Enrollment;
import org.intecbrussel.model.Role;
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

    /**
     * POST /api/courses/{id}/enroll
     * - STUDENT: schrijft zichzelf in
     * - ADMIN: schrijft student in via ?studentId=...
     */
    @PostMapping("/courses/{courseId}/enroll")
    @PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
    public ResponseEntity<EnrollmentResponse> enroll(
            @PathVariable Long courseId,
            @RequestParam(required = false) Long studentId
    ) {
        User current = getCurrentUser();

        Enrollment enrollment;
        if (current.getRole() == Role.ADMIN) {
            if (studentId == null) {
                throw new UnauthorizedActionException("studentId is required for admin enrollment");
            }
            enrollment = enrollmentService.enrollAsAdmin(courseId, studentId);
        } else {
            enrollment = enrollmentService.enrollSelf(courseId, current);
        }

        return ResponseEntity.ok(toResponse(enrollment));
    }

    /**
     * GET /api/enrollments/me
     * - STUDENT: ziet alleen eigen enrollments
     * - ADMIN: mag dit endpoint ook gebruiken (zal meestal leeg zijn tenzij admin ook enrollments heeft)
     */
    @GetMapping("/enrollments/me")
    @PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
    public ResponseEntity<List<EnrollmentResponse>> myEnrollments() {
        User current = getCurrentUser();

        return ResponseEntity.ok(
                enrollmentService.getStudentEnrollments(current)
                        .stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    /**
     * GET /api/instructor/enrollments
     * - INSTRUCTOR: ziet enrollments van eigen courses
     */
    @GetMapping("/instructor/enrollments")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<List<EnrollmentResponse>> instructorEnrollments() {
        return ResponseEntity.ok(
                enrollmentService.getInstructorEnrollments(getCurrentUser())
                        .stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    /**
     * GET /api/admin/enrollments
     * - ADMIN: ziet alle enrollments
     */
    @GetMapping("/admin/enrollments")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<EnrollmentResponse>> allEnrollments() {
        return ResponseEntity.ok(
                enrollmentService.getAllEnrollments()
                        .stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    /**
     * DELETE /api/enrollments/{id}
     * - STUDENT: kan alleen eigen enrollment annuleren (service check)
     * - ADMIN: mag alles annuleren
     */
    @DeleteMapping("/enrollments/{id}")
    @PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
    public ResponseEntity<String> cancelEnrollment(@PathVariable Long id) {
        enrollmentService.cancelEnrollment(id, getCurrentUser());
        return ResponseEntity.ok("Enrollment canceled successfully");
    }

    // ===== Helpers =====
    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
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
