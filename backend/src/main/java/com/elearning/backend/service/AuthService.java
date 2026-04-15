package com.elearning.backend.service;

import com.elearning.backend.dto.*;
import com.elearning.backend.model.User;
import com.elearning.backend.repository.UserRepository;
import com.elearning.backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    // ===== REGISTER =====
    public AuthResponse register(RegisterRequest request) {

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use");
        }

        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already taken");
        }

        // Create new user
        User user = new User();
        user.setFullName(request.getFullName());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // encrypt!
        user.setRole(User.Role.STUDENT);

        userRepository.save(user);

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getEmail());

        return new AuthResponse(
                token,
                user.getUsername(),
                user.getEmail(),
                user.getRole().name(),
                "Registration successful!"
        );
    }

    // ===== LOGIN =====
    public AuthResponse login(LoginRequest request) {

        // Find user by email
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        System.out.println("User found: " + userOpt.isPresent());

        User user = userOpt.orElseThrow(
                () -> new RuntimeException("Invalid email or password"));

        System.out.println("Stored password hash: " + user.getPassword());
        System.out.println("Password matches: " +
                passwordEncoder.matches(request.getPassword(), user.getPassword()));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getEmail());

        System.out.println("Login successful for: " + user.getEmail());

        return new AuthResponse(
                token,
                user.getUsername(),
                user.getEmail(),
                user.getRole().name(),
                "Login successful!"
        );
    }

    // ===== FORGOT PASSWORD =====
    public String forgotPassword(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No account found with this email"));

        // Generate a unique reset token
        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(15)); // expires in 15 min
        userRepository.save(user);

        // Send reset email
        emailService.sendResetEmail(user.getEmail(), token);

        return "Password reset link sent to your email!";
    }

    // ===== RESET PASSWORD =====
    public String resetPassword(ResetPasswordRequest request) {

        // Trim any whitespace/encoding issues from token
        String cleanToken = request.getToken().trim();

        User user = userRepository.findByResetToken(cleanToken)
                .orElseThrow(() -> new RuntimeException(
                        "Invalid reset link. Please request a new one."));

        // Check expiry
        if (user.getResetTokenExpiry() == null ||
                user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException(
                    "Reset link has expired. Please request a new one.");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);

        return "Password reset successfully! You can now login.";
    }

    // ===== REQUEST TO BECOME INSTRUCTOR =====
    public String requestInstructor(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() != User.Role.STUDENT) {
            throw new RuntimeException("Only students can request instructor role");
        }

        user.setInstructorStatus(User.InstructorStatus.PENDING);
        userRepository.save(user);

        return "Your request has been submitted! Admin will review it soon.";
    }

    // ===== ADMIN: APPROVE OR REJECT INSTRUCTOR =====
    public String handleInstructorRequest(Long userId, boolean approve) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (approve) {
            user.setRole(User.Role.INSTRUCTOR);
            user.setInstructorStatus(User.InstructorStatus.APPROVED);
        } else {
            user.setInstructorStatus(User.InstructorStatus.REJECTED);
        }

        userRepository.save(user);
        return approve ? "Instructor approved!" : "Request rejected.";
    }
}