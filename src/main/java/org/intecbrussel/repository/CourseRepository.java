package org.intecbrussel.repository;

import org.intecbrussel.model.Course;
import org.intecbrussel.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    // handig als je instructor enkel eigen courses mag beheren
    List<Course> findByInstructor(User instructor);
}
