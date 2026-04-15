package com.elearning.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class QuizRequest {
    private String title;
    private String description;
    private Integer passingScore;
    private Integer timeLimitMinutes;
    private List<QuestionRequest> questions;
}