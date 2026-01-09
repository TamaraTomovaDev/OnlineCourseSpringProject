package org.intecbrussel.repository;

import org.intecbrussel.model.Enrollment;
import org.intecbrussel.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    List<Enrollment> findByStudent(User student);

    List<Enrollment> findByCourseInstructor(User instructor);

    boolean existsByStudentAndCourse(User student, org.intecbrussel.model.Course course);
}
