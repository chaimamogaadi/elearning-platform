package com.elearning.backend.dto;

import lombok.Data;
import java.util.Map;

@Data
public class QuizSubmissionRequest {
    private Long quizId;
    // Map of questionId → student's answer
    private Map<Long, String> answers;
}