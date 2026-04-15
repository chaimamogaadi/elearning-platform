package com.elearning.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "enrollments")
@Data
@NoArgsConstructor
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    @JsonIgnoreProperties({
            "password", "resetToken",
            "enrollments", "courses"
    })
    private User student;

    @ManyToOne
    @JoinColumn(name = "course_id")
    @JsonIgnoreProperties({
            "instructor", "enrollments"
    })
    private Course course;

    private LocalDateTime enrolledAt = LocalDateTime.now();
}