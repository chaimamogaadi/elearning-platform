package com.elearning.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "courses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    // ✅ Only show basic instructor info
    @ManyToOne
    @JoinColumn(name = "instructor_id")
    @JsonIgnoreProperties({
            "password", "resetToken",
            "resetTokenExpiry", "courses",
            "enrollments"
    })
    private User instructor;

    private Double price       = 0.0;
    private String category;
    private String thumbnailUrl;
    private Integer totalLessons = 0;

    @Enumerated(EnumType.STRING)
    private CourseStatus status = CourseStatus.DRAFT;

    private LocalDateTime createdAt = LocalDateTime.now();

    public enum CourseStatus {
        PENDING, APPROVED, REJECTED, DRAFT
    }
}