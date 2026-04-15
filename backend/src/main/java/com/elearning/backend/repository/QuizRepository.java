package com.elearning.backend.repository;

import com.elearning.backend.model.Quiz;
import com.elearning.backend.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findByCourse(Course course);
}