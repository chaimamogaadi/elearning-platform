package com.elearning.backend.repository;

import com.elearning.backend.model.Course;
import com.elearning.backend.model.Transaction;
import com.elearning.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository
        extends JpaRepository<Transaction, Long> {

    List<Transaction> findByStudent(User student);
    Optional<Transaction> findByStripePaymentIntentId(
            String paymentIntentId);
    List<Transaction> findByStatus(
            Transaction.TransactionStatus status);
    // Find pending transaction for retry cleanup
    Optional<Transaction> findByStudentAndCourseAndStatus(
            User student,
            Course course,
            Transaction.TransactionStatus status
    );
}