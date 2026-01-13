package org.intecbrussel.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.intecbrussel.model.Course;
import org.intecbrussel.model.User;
import org.intecbrussel.model.Role;
import org.intecbrussel.repository.CourseRepository;
import org.intecbrussel.exception.ResourceNotFoundException;
import org.intecbrussel.exception.UnauthorizedActionException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseService {

    private final CourseRepository courseRepository;

    public List<Course> listAll() {
        return courseRepository.findAll();
    }

    public Course getById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + id));
    }

    public Course createCourse(String title, String description, User instructor) {
        if (instructor.getRole() != Role.INSTRUCTOR && instructor.getRole() != Role.ADMIN) {
            throw new UnauthorizedActionException("Only instructors or admins can create courses");
        }
        Course course = new Course();
        course.setTitle(title);
        course.setDescription(description);
        course.setInstructor(instructor);
        return courseRepository.save(course);
    }

    public Course updateCourse(Long courseId, String title, String description, User user) {
        Course course = getById(courseId);

        // student mag nooit
        if (user.getRole() == Role.STUDENT) {
            throw new UnauthorizedActionException("Students cannot update courses");
        }

        // instructor alleen eigen course
        if (user.getRole() == Role.INSTRUCTOR &&
                !course.getInstructor().getId().equals(user.getId())) {
            throw new UnauthorizedActionException("You can only update your own courses");
        }

        course.setTitle(title);
        course.setDescription(description);
        return courseRepository.save(course);
    }

    public void deleteCourse(Long courseId, User user) {
        if (user.getRole() != Role.ADMIN) {
            throw new UnauthorizedActionException("Only admins can delete courses");
        }
        Course course = getById(courseId);
        courseRepository.delete(course);
    }
}
