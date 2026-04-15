package com.elearning.backend.repository;

import com.elearning.backend.model.Course;
import com.elearning.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findByInstructor(User instructor);
    List<Course> findByStatus(Course.CourseStatus status);
    long countByStatus(Course.CourseStatus status);
    List<Course> findByTitleContainingIgnoreCase(String title);
}