package org.intecbrussel.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.intecbrussel.model.Course;
import org.intecbrussel.model.Enrollment;
import org.intecbrussel.model.User;
import org.intecbrussel.model.Role;
import org.intecbrussel.repository.EnrollmentRepository;
import org.intecbrussel.repository.CourseRepository;
import org.intecbrussel.exception.ResourceNotFoundException;
import org.intecbrussel.exception.DuplicateEnrollmentException;
import org.intecbrussel.exception.UnauthorizedActionException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;

    // ---------------- Enroll student ----------------
    public Enrollment enrollStudent(Long courseId, User student) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + courseId));

        boolean exists = enrollmentRepository.existsByStudentAndCourse(student, course);
        if (exists) {
            throw new DuplicateEnrollmentException("Student already enrolled in this course");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setEnrollmentDate(LocalDateTime.now());
        return enrollmentRepository.save(enrollment);
    }

    // ---------------- List enrollments ----------------
    public List<Enrollment> listEnrollments(User user) {
        if (user.getRole() == Role.STUDENT) {
            return enrollmentRepository.findByStudent(user);
        } else if (user.getRole() == Role.INSTRUCTOR) {
            return enrollmentRepository.findAll()
                    .stream()
                    .filter(e -> e.getCourse().getInstructor().getId().equals(user.getId()))
                    .collect(Collectors.toList());
        } else if (user.getRole() == Role.ADMIN) {
            return enrollmentRepository.findAll();
        }
        return List.of();
    }

    // ---------------- Cancel enrollment ----------------
    public void cancelEnrollment(Long enrollmentId, User user) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found: " + enrollmentId));

        if (user.getRole() == Role.STUDENT && !enrollment.getStudent().getId().equals(user.getId())) {
            throw new UnauthorizedActionException("You can only cancel your own enrollment");
        }

        enrollmentRepository.delete(enrollment);
    }
}
