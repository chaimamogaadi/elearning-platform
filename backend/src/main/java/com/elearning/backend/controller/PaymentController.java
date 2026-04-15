package com.elearning.backend.controller;

import com.elearning.backend.dto.*;
import com.elearning.backend.model.Transaction;
import com.elearning.backend.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // POST /api/payment/create-intent
    // Step 1: Student clicks enroll → create payment intent
    @PostMapping("/create-intent")
    public ResponseEntity<PaymentIntentResponse> createIntent(
            @RequestBody PaymentIntentRequest request,
            Authentication auth) {
        try {
            return ResponseEntity.ok(
                    paymentService.createPaymentIntent(
                            request.getCourseId(),
                            auth.getName()
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // POST /api/payment/confirm
    // Step 2: After Stripe confirms on frontend
    @PostMapping("/confirm")
    public ResponseEntity<Map<String, String>> confirm(
            @RequestParam String paymentIntentId,
            Authentication auth) {
        try {
            return ResponseEntity.ok(
                    paymentService.confirmPayment(
                            paymentIntentId,
                            auth.getName()
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // GET /api/payment/my-transactions
    @GetMapping("/my-transactions")
    public ResponseEntity<List<Transaction>> myTransactions(
            Authentication auth) {
        return ResponseEntity.ok(
                paymentService.getMyTransactions(auth.getName()));
    }
}