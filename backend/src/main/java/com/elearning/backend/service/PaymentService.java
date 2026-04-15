package com.elearning.backend.service;

import com.elearning.backend.dto.*;
import com.elearning.backend.model.*;
import com.elearning.backend.repository.*;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final CourseRepository      courseRepository;
    private final UserRepository        userRepository;
    private final TransactionRepository transactionRepository;
    private final EnrollmentRepository  enrollmentRepository;

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @Value("${stripe.publishable.key}")
    private String stripePublishableKey;

    // ===== CREATE PAYMENT INTENT =====
    // ===== CREATE PAYMENT INTENT =====
    public PaymentIntentResponse createPaymentIntent(
            Long courseId,
            String studentEmail) throws Exception {

        Stripe.apiKey = stripeSecretKey;

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() ->
                        new RuntimeException("Course not found"));

        User student = userRepository
                .findByEmail(studentEmail)
                .orElseThrow(() ->
                        new RuntimeException("Student not found"));

        // Already enrolled check
        if (enrollmentRepository.existsByStudentAndCourse(
                student, course)) {
            throw new RuntimeException(
                    "You are already enrolled in this course");
        }

        // ✅ Delete any old PENDING transactions
        // so student can try again after a failed attempt
        transactionRepository
                .findByStudentAndCourseAndStatus(
                        student, course,
                        Transaction.TransactionStatus.PENDING)
                .ifPresent(transactionRepository::delete);

        long amountInCents =
                Math.round(course.getPrice() * 100);

        PaymentIntentCreateParams params =
                PaymentIntentCreateParams.builder()
                        .setAmount(amountInCents)
                        .setCurrency("usd")
                        .putMetadata("courseId",
                                courseId.toString())
                        .putMetadata("studentEmail",
                                studentEmail)
                        .putMetadata("courseName",
                                course.getTitle())
                        .setAutomaticPaymentMethods(
                                PaymentIntentCreateParams
                                        .AutomaticPaymentMethods
                                        .builder()
                                        .setEnabled(true)
                                        .build())
                        .build();

        PaymentIntent intent =
                PaymentIntent.create(params);

        Transaction transaction = new Transaction();
        transaction.setStudent(student);
        transaction.setCourse(course);
        transaction.setStripePaymentIntentId(
                intent.getId());
        transaction.setAmount(course.getPrice());
        transaction.setStatus(
                Transaction.TransactionStatus.PENDING);
        Transaction saved =
                transactionRepository.save(transaction);

        return new PaymentIntentResponse(
                intent.getClientSecret(),
                stripePublishableKey,
                saved.getId(),
                course.getPrice(),
                course.getTitle()
        );
    }

    // ===== WEBHOOK: PAYMENT SUCCEEDED =====
    // Called automatically by Stripe via webhook
    public void handleSuccessfulPayment(
            String paymentIntentId) {

        transactionRepository
                .findByStripePaymentIntentId(paymentIntentId)
                .ifPresentOrElse(
                        transaction -> {
                            // Update status
                            transaction.setStatus(
                                    Transaction.TransactionStatus
                                            .SUCCEEDED);
                            transaction.setUpdatedAt(
                                    LocalDateTime.now());
                            transactionRepository.save(transaction);

                            // Enroll student automatically
                            User student = transaction.getStudent();
                            Course course = transaction.getCourse();

                            if (!enrollmentRepository
                                    .existsByStudentAndCourse(
                                            student, course)) {
                                Enrollment enrollment =
                                        new Enrollment();
                                enrollment.setStudent(student);
                                enrollment.setCourse(course);
                                enrollmentRepository.save(enrollment);

                                System.out.println(
                                        "✅ Student " +
                                                student.getEmail() +
                                                " enrolled in: " +
                                                course.getTitle());
                            }
                        },
                        () -> System.out.println(
                                "⚠️ Transaction not found for: "
                                        + paymentIntentId)
                );
    }

    // ===== WEBHOOK: PAYMENT FAILED =====
    public void handleFailedPayment(
            String paymentIntentId) {

        transactionRepository
                .findByStripePaymentIntentId(paymentIntentId)
                .ifPresent(t -> {
                    t.setStatus(
                            Transaction.TransactionStatus.FAILED);
                    t.setUpdatedAt(LocalDateTime.now());
                    transactionRepository.save(t);
                    System.out.println(
                            "❌ Payment failed saved for: "
                                    + paymentIntentId);
                });
    }

    // ===== FRONTEND CONFIRMATION =====
    // Called by Angular after Stripe confirms on frontend
    // This is a backup in case webhook is slow
    public Map<String, String> confirmPayment(
            String paymentIntentId,
            String studentEmail) throws Exception {

        Stripe.apiKey = stripeSecretKey;

        // Double-check with Stripe directly
        PaymentIntent intent =
                PaymentIntent.retrieve(paymentIntentId);

        if (!"succeeded".equals(intent.getStatus())) {
            throw new RuntimeException(
                    "Payment not confirmed by Stripe");
        }

        // Trigger the same logic as webhook
        // (safe to call twice — enrollment check prevents duplicates)
        handleSuccessfulPayment(paymentIntentId);

        Transaction transaction = transactionRepository
                .findByStripePaymentIntentId(paymentIntentId)
                .orElseThrow(() ->
                        new RuntimeException("Transaction not found"));

        return Map.of("message",
                "Payment successful! You are now enrolled in "
                        + transaction.getCourse().getTitle());
    }

    // ===== GET STUDENT TRANSACTIONS =====
    public List<Transaction> getMyTransactions(
            String studentEmail) {
        User student = userRepository
                .findByEmail(studentEmail)
                .orElseThrow(() ->
                        new RuntimeException("Student not found"));
        return transactionRepository
                .findByStudent(student);
    }
}