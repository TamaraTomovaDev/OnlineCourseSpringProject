package org.intecbrussel.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.intecbrussel.model.Course;
import org.intecbrussel.model.User;
import org.intecbrussel.repository.CourseRepository;
import org.intecbrussel.exception.ResourceNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseService {

    private final CourseRepository courseRepository;

    // ---------------- List all courses ----------------
    public List<Course> listCourses() {
        return courseRepository.findAll();
    }

    // ---------------- Create course ----------------
    public Course createCourse(Course course, User instructor) {
        course.setInstructor(instructor);
        return courseRepository.save(course);
    }

    // ---------------- Update course ----------------
    public Course updateCourse(Long courseId, Course updatedCourse, User user) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        // Alleen ADMIN of de eigen instructor mag updaten
        if (!course.getInstructor().getId().equals(user.getId()) && user.getRole() != org.intecbrussel.model.Role.ADMIN) {
            throw new RuntimeException("You are not allowed to update this course");
        }

        course.setTitle(updatedCourse.getTitle());
        course.setDescription(updatedCourse.getDescription());
        return courseRepository.save(course);
    }

    // ---------------- Delete course ----------------
    public void deleteCourse(Long courseId, User user) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        // Alleen ADMIN mag verwijderen
        if (user.getRole() != org.intecbrussel.model.Role.ADMIN) {
            throw new RuntimeException("Only ADMIN can delete courses");
        }

        courseRepository.delete(course);
    }
}
