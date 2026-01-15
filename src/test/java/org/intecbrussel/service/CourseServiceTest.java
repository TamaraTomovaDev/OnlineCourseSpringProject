package org.intecbrussel.service;

import org.intecbrussel.exception.ResourceNotFoundException;
import org.intecbrussel.exception.UnauthorizedActionException;
import org.intecbrussel.model.Course;
import org.intecbrussel.model.Role;
import org.intecbrussel.model.User;
import org.intecbrussel.repository.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CourseService courseService;

    private User admin;
    private User instructor;
    private User student;
    private Course course;

    @BeforeEach
    void setUp() {
        admin = new User();
        admin.setId(1L);
        admin.setRole(Role.ADMIN);

        instructor = new User();
        instructor.setId(2L);
        instructor.setRole(Role.INSTRUCTOR);

        student = new User();
        student.setId(3L);
        student.setRole(Role.STUDENT);

        course = new Course();
        course.setId(10L);
        course.setInstructor(instructor);
        course.setTitle("Test Course");
        course.setDescription("Test Description");
    }

    @Test
    void createCourse_AsInstructor_Succeeds() {
        when(courseRepository.save(any(Course.class))).thenReturn(course);

        Course created = courseService.createCourse("Test Course", "Test Description", instructor);

        assertEquals("Test Course", created.getTitle());
        assertEquals("Test Description", created.getDescription());
        assertEquals(instructor, created.getInstructor());

        verify(courseRepository).save(any(Course.class));
    }

    @Test
    void createCourse_AsStudent_ThrowsUnauthorized() {
        UnauthorizedActionException ex = assertThrows(UnauthorizedActionException.class,
                () -> courseService.createCourse("Test Course", "Test Description", student));
        assertEquals("Only instructors or admins can create courses", ex.getMessage());

        verifyNoInteractions(courseRepository);
    }

    @Test
    void updateCourse_AsInstructorOwnCourse_Succeeds() {
        when(courseRepository.findById(course.getId())).thenReturn(Optional.of(course));
        when(courseRepository.save(course)).thenReturn(course);

        Course updated = courseService.updateCourse(course.getId(), "Updated", "Updated Desc", instructor);

        assertEquals("Updated", updated.getTitle());
        assertEquals("Updated Desc", updated.getDescription());
    }

    @Test
    void deleteCourse_AsNonAdmin_ThrowsUnauthorized() {
        ResourceNotFoundException notFoundException = null;

        UnauthorizedActionException ex = assertThrows(UnauthorizedActionException.class,
                () -> courseService.deleteCourse(course.getId(), instructor));

        assertEquals("Only admins can delete courses", ex.getMessage());

        verify(courseRepository, never()).delete(any());
    }

    @Test
    void deleteCourse_AsAdmin_Succeeds() {
        when(courseRepository.findById(course.getId())).thenReturn(Optional.of(course));

        courseService.deleteCourse(course.getId(), admin);

        verify(courseRepository).delete(course);
    }

    @Test
    void getById_NotFound_ThrowsResourceNotFound() {
        when(courseRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> courseService.getById(999L));

        assertEquals("Course not found: 999", ex.getMessage());
    }
}
