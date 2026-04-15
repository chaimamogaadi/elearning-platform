package com.elearning.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "lessons")
@Data
@NoArgsConstructor
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ Don't serialize full course inside lesson
    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    @JsonIgnoreProperties({
            "lessons", "instructor",
            "enrollments", "quizzes"
    })
    private Course course;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String videoUrl;
    private Integer orderNum         = 0;
    private Integer durationMinutes  = 0;

    @Enumerated(EnumType.STRING)
    private LessonType type = LessonType.TEXT;

    private LocalDateTime createdAt = LocalDateTime.now();

    public enum LessonType {
        TEXT, VIDEO, MIXED
    }
}