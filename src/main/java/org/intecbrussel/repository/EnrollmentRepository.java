package org.intecbrussel.repository;

import org.intecbrussel.model.Course;
import org.intecbrussel.model.Enrollment;
import org.intecbrussel.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    // student "me"
    List<Enrollment> findByStudent(User student);

    // instructor enrollments van eigen courses
    List<Enrollment> findByCourseInstructor(User instructor);

    // duplicate check
    boolean existsByStudentAndCourse(User student, Course course);
}
