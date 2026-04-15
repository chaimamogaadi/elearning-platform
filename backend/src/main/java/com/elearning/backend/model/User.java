package com.elearning.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    // ✅ Never send password in API responses
    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role = Role.STUDENT;

    @Enumerated(EnumType.STRING)
    private InstructorStatus instructorStatus =
            InstructorStatus.NONE;

    // ✅ Never send reset tokens in responses
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String resetToken;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private LocalDateTime resetTokenExpiry;

    private Boolean isActive = true;
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum Role {
        STUDENT, INSTRUCTOR, ADMIN
    }

    public enum InstructorStatus {
        NONE, PENDING, APPROVED, REJECTED
    }
}