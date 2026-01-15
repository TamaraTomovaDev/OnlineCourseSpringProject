package org.intecbrussel.service;

import org.intecbrussel.exception.DuplicateEnrollmentException;
import org.intecbrussel.exception.UnauthorizedActionException;
import org.intecbrussel.model.Course;
import org.intecbrussel.model.Enrollment;
import org.intecbrussel.model.Role;
import org.intecbrussel.model.User;
import org.intecbrussel.repository.CourseRepository;
import org.intecbrussel.repository.EnrollmentRepository;
import org.intecbrussel.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EnrollmentService enrollmentService;

    private User student;
    private User instructor;
    private User admin;
    private Course course;
    private Enrollment enrollment;

    @BeforeEach
    void setUp() {
        student = new User();
        student.setId(1L);
        student.setRole(Role.STUDENT);

        instructor = new User();
        instructor.setId(2L);
        instructor.setRole(Role.INSTRUCTOR);

        admin = new User();
        admin.setId(3L);
        admin.setRole(Role.ADMIN);

        course = new Course();
        course.setId(10L);
        course.setInstructor(instructor);

        enrollment = new Enrollment();
        enrollment.setId(100L);
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setEnrollmentDate(LocalDateTime.now());
    }

    @Test
    void enrollSelf_AsStudent_Succeeds() {
        when(courseRepository.findById(course.getId())).thenReturn(Optional.of(course));
        when(enrollmentRepository.existsByStudentAndCourse(student, course)).thenReturn(false);
        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(enrollment);

        Enrollment result = enrollmentService.enrollSelf(course.getId(), student);

        assertEquals(student, result.getStudent());
        assertEquals(course, result.getCourse());
        verify(enrollmentRepository).save(any(Enrollment.class));
    }

    @Test
    void enrollSelf_AsNonStudent_ThrowsUnauthorized() {
        UnauthorizedActionException ex = assertThrows(UnauthorizedActionException.class,
                () -> enrollmentService.enrollSelf(course.getId(), instructor));
        assertEquals("Only students can enroll themselves", ex.getMessage());
        verifyNoInteractions(enrollmentRepository);
    }

    @Test
    void enrollSelf_AlreadyEnrolled_ThrowsDuplicateEnrollment() {
        when(courseRepository.findById(course.getId())).thenReturn(Optional.of(course));
        when(enrollmentRepository.existsByStudentAndCourse(student, course)).thenReturn(true);

        DuplicateEnrollmentException ex = assertThrows(DuplicateEnrollmentException.class,
                () -> enrollmentService.enrollSelf(course.getId(), student));
        assertEquals("Student already enrolled in this course", ex.getMessage());
        verify(enrollmentRepository, never()).save(any());
    }

    @Test
    void getStudentEnrollments_ReturnsList() {
        when(enrollmentRepository.findByStudent(student)).thenReturn(List.of(enrollment));

        List<Enrollment> result = enrollmentService.getStudentEnrollments(student);

        assertEquals(1, result.size());
        assertEquals(enrollment, result.get(0));
    }

    @Test
    void cancelEnrollment_AsStudentOwnEnrollment_Succeeds() {
        when(enrollmentRepository.findById(enrollment.getId())).thenReturn(Optional.of(enrollment));

        enrollmentService.cancelEnrollment(enrollment.getId(), student);

        verify(enrollmentRepository).delete(enrollment);
    }

    @Test
    void cancelEnrollment_AsStudentOtherEnrollment_ThrowsUnauthorized() {
        User otherStudent = new User();
        otherStudent.setId(999L);
        otherStudent.setRole(Role.STUDENT);

        when(enrollmentRepository.findById(enrollment.getId())).thenReturn(Optional.of(enrollment));

        UnauthorizedActionException ex = assertThrows(UnauthorizedActionException.class,
                () -> enrollmentService.cancelEnrollment(enrollment.getId(), otherStudent));
        assertEquals("You can only cancel your own enrollment", ex.getMessage());
        verify(enrollmentRepository, never()).delete(any());
    }

    @Test
    void cancelEnrollment_AsAdmin_Succeeds() {
        when(enrollmentRepository.findById(enrollment.getId())).thenReturn(Optional.of(enrollment));

        enrollmentService.cancelEnrollment(enrollment.getId(), admin);

        verify(enrollmentRepository).delete(enrollment);
    }

    @Test
    void getInstructorEnrollments_AsNonInstructor_ThrowsUnauthorized() {
        UnauthorizedActionException ex = assertThrows(UnauthorizedActionException.class,
                () -> enrollmentService.getInstructorEnrollments(admin));
        assertEquals("Only instructors can view these enrollments", ex.getMessage());
    }
}
