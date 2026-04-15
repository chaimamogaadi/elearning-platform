package com.elearning.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "quizzes")
@Data
@NoArgsConstructor
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ Break circular reference — don't serialize
    // the full course object inside quiz
    @ManyToOne
    @JoinColumn(name = "course_id")
    @JsonIgnoreProperties({
            "instructor", "enrollments",
            "lessons", "quizzes"
    })
    private Course course;

    private String title;
    private String description;
    private Integer passingScore    = 70;
    private Integer timeLimitMinutes = 30;

    // ✅ Don't serialize quiz back reference in questions
    @OneToMany(mappedBy = "quiz",
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER)
    @JsonIgnoreProperties("quiz")
    private List<Question> questions;

    private LocalDateTime createdAt = LocalDateTime.now();
}