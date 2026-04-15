package com.elearning.backend.dto;

import lombok.Data;

@Data
public class LessonRequest {
    private String title;
    private String content;
    private String videoUrl;
    private Integer orderNum;
    private Integer durationMinutes;
    private String type; // TEXT, VIDEO, MIXED
}