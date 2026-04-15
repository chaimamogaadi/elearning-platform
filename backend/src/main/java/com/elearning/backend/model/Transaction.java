package com.elearning.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    // Stripe payment intent ID — for tracking
    @Column(unique = true)
    private String stripePaymentIntentId;

    private Double amount;
    private String currency = "usd";

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    public enum TransactionStatus {
        PENDING,    // payment initiated
        SUCCEEDED,  // payment successful
        FAILED,     // payment failed
        REFUNDED    // money returned
    }
}