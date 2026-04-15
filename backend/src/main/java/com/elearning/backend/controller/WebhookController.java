package com.elearning.backend.controller;

import com.elearning.backend.service.PaymentService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class WebhookController {

    private final PaymentService paymentService;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    // Stripe will POST to this endpoint automatically
    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            // Raw body from Stripe
            @RequestBody String payload,
            // Stripe signature header for verification
            @RequestHeader("Stripe-Signature") String sigHeader) {

        Event event;

        try {
            // ✅ Verify the request actually came from Stripe
            // This prevents fake requests from bad actors
            event = Webhook.constructEvent(
                    payload, sigHeader, webhookSecret);

        } catch (SignatureVerificationException e) {
            // Signature didn't match — reject the request
            System.out.println("❌ Invalid Stripe signature!");
            return ResponseEntity.badRequest()
                    .body("Invalid signature");
        }

        System.out.println(
                "✅ Stripe webhook received: " + event.getType());

        // Handle different event types
        switch (event.getType()) {

            case "payment_intent.succeeded" -> {
                // Payment was successful!
                PaymentIntent intent = (PaymentIntent)
                        event.getDataObjectDeserializer()
                                .getObject()
                                .orElse(null);

                if (intent != null) {
                    System.out.println(
                            "💰 Payment succeeded: " + intent.getId());
                    paymentService.handleSuccessfulPayment(
                            intent.getId());
                }
            }

            case "payment_intent.payment_failed" -> {
                // Payment failed
                PaymentIntent intent = (PaymentIntent)
                        event.getDataObjectDeserializer()
                                .getObject()
                                .orElse(null);

                if (intent != null) {
                    System.out.println(
                            "❌ Payment failed: " + intent.getId());
                    paymentService.handleFailedPayment(
                            intent.getId());
                }
            }

            default -> System.out.println(
                    "Unhandled event: " + event.getType());
        }

        // Always return 200 to Stripe so it knows we received it
        return ResponseEntity.ok("Webhook received");
    }
}