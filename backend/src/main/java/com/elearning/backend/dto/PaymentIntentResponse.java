package com.elearning.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentIntentResponse {
    private String clientSecret;   // sent to Angular
    private String publishableKey; // Stripe public key
    private Long   transactionId;  // our DB record ID
    private Double amount;
    private String courseName;
}