package com.elearning.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class QuestionRequest {
    private String questionText;
    private List<String> options;   // ["Option A", "Option B", ...]
    private String correctAnswer;
    private Integer points;
}