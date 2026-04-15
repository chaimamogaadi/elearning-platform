package com.elearning.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "certificates")
@Data
@NoArgsConstructor
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Unique certificate ID shown on the certificate
    @Column(unique = true, nullable = false)
    private String certificateNumber;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    private Integer quizScore;      // score they got
    private Integer quizPercentage; // percentage

    private LocalDateTime issuedAt = LocalDateTime.now();

    // Auto-generate certificate number before saving
    @PrePersist
    public void generateCertNumber() {
        this.certificateNumber =
                "CERT-" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8)
                                .toUpperCase();
    }
}