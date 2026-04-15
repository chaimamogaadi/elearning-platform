package com.elearning.backend.controller;

import com.elearning.backend.dto.*;
import com.elearning.backend.model.User;
import com.elearning.backend.repository.UserRepository;
import com.elearning.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // ✅ Now returns JSON: { "message": "..." }
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        String result = authService.forgotPassword(request.getEmail());
        return ResponseEntity.ok(Map.of("message", result));
    }

    // ✅ Now returns JSON: { "message": "..." }
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        String result = authService.resetPassword(request);
        return ResponseEntity.ok(Map.of("message", result));
    }
    // GET /api/auth/me — get logged in user info
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe(Authentication auth) {
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(UserResponse.fromUser(user));
    }

    // POST /api/auth/request-instructor
    @PostMapping("/request-instructor")
    public ResponseEntity<Map<String, String>> requestInstructor(
            Authentication auth) {
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() != User.Role.STUDENT) {
            throw new RuntimeException(
                    "Only students can request instructor role");
        }

        if (user.getInstructorStatus() == User.InstructorStatus.PENDING) {
            throw new RuntimeException(
                    "You already have a pending request");
        }

        user.setInstructorStatus(User.InstructorStatus.PENDING);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of(
                "message",
                "Request submitted! Admin will review it soon."
        ));
    }
}