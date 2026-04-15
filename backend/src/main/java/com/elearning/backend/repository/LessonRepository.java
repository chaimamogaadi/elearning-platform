package com.elearning.backend.repository;

import com.elearning.backend.model.Lesson;
import com.elearning.backend.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findByCourseOrderByOrderNumAsc(Course course);
    long countByCourse(Course course);
}