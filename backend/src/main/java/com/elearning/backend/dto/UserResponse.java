package com.elearning.backend.dto;

import com.elearning.backend.model.User;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserResponse {
    private Long id;
    private String fullName;
    private String username;
    private String email;
    private String role;
    private String instructorStatus;
    private Boolean isActive;
    private LocalDateTime createdAt;

    // Convert User entity to UserResponse
    public static UserResponse fromUser(User user) {
        UserResponse res = new UserResponse();
        res.setId(user.getId());
        res.setFullName(user.getFullName());
        res.setUsername(user.getUsername());
        res.setEmail(user.getEmail());
        res.setRole(user.getRole().name());
        res.setInstructorStatus(
                user.getInstructorStatus() != null
                        ? user.getInstructorStatus().name()
                        : "NONE"
        );
        res.setIsActive(user.getIsActive());
        res.setCreatedAt(user.getCreatedAt());
        return res;
    }
}