package com.elearning.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class QuizResultResponse {
    private Integer score;
    private Integer totalPoints;
    private Integer percentage;
    private Boolean passed;
    private String  message;
    private List<QuestionResultDto> questionResults;

    @Data
    @AllArgsConstructor
    public static class QuestionResultDto {
        private Long    questionId;
        private String  questionText;
        private String  studentAnswer;
        private String  correctAnswer;
        private Boolean isCorrect;
        private Integer points;
    }
}