package com.elearning.backend.dto;

import lombok.Data;

@Data
public class CourseRequest {
    private String title;
    private String description;
    private Double price;
    private String category;
    private String thumbnailUrl;
}