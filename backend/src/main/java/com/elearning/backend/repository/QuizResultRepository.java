package com.elearning.backend.repository;

import com.elearning.backend.model.QuizResult;
import com.elearning.backend.model.User;
import com.elearning.backend.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuizResultRepository extends JpaRepository<QuizResult, Long> {
    List<QuizResult> findByStudent(User student);
    List<QuizResult> findByQuiz(Quiz quiz);
    boolean existsByStudentAndQuiz(User student, Quiz quiz);
}