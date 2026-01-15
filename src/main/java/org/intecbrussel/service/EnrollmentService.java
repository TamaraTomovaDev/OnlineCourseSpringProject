package org.intecbrussel.service;

import lombok.RequiredArgsConstructor;
import org.intecbrussel.exception.DuplicateEnrollmentException;
import org.intecbrussel.exception.ResourceNotFoundException;
import org.intecbrussel.exception.UnauthorizedActionException;
import org.intecbrussel.model.Course;
import org.intecbrussel.model.Enrollment;
import org.intecbrussel.model.Role;
import org.intecbrussel.model.User;
import org.intecbrussel.repository.CourseRepository;
import org.intecbrussel.repository.EnrollmentRepository;
import org.intecbrussel.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    // ENROLL

    // STUDENT → zichzelf inschrijven
    public Enrollment enrollSelf(Long courseId, User student) {
        if (student.getRole() != Role.STUDENT) {
            throw new UnauthorizedActionException("Only students can enroll themselves");
        }
        return enroll(courseId, student);
    }

    // ADMIN → student inschrijven
    public Enrollment enrollAsAdmin(Long courseId, Long studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Student not found: " + studentId));

        if (student.getRole() != Role.STUDENT) {
            throw new UnauthorizedActionException("Only students can be enrolled");
        }

        return enroll(courseId, student);
    }

    private Enrollment enroll(Long courseId, User student) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Course not found: " + courseId));

        if (enrollmentRepository.existsByStudentAndCourse(student, course)) {
            throw new DuplicateEnrollmentException("Student already enrolled in this course");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setEnrollmentDate(LocalDateTime.now());

        return enrollmentRepository.save(enrollment);
    }

    // LIST

    // STUDENT → alleen eigen enrollments
    public List<Enrollment> getStudentEnrollments(User student) {
        return enrollmentRepository.findByStudent(student);
    }

    // INSTRUCTOR → enrollments van eigen courses
    public List<Enrollment> getInstructorEnrollments(User instructor) {
        if (instructor.getRole() != Role.INSTRUCTOR) {
            throw new UnauthorizedActionException("Only instructors can view these enrollments");
        }
        return enrollmentRepository.findByCourseInstructor(instructor);
    }

    // ADMIN → alles
    public List<Enrollment> getAllEnrollments() {
        return enrollmentRepository.findAll();
    }

    // CANCEL

    public void cancelEnrollment(Long enrollmentId, User user) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Enrollment not found: " + enrollmentId));

        // STUDENT mag alleen eigen enrollment annuleren
        if (user.getRole() == Role.STUDENT &&
                !enrollment.getStudent().getId().equals(user.getId())) {
            throw new UnauthorizedActionException("You can only cancel your own enrollment");
        }

        enrollmentRepository.delete(enrollment);
    }
}