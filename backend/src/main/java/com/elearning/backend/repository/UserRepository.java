package com.elearning.backend.repository;

import com.elearning.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findByResetToken(String resetToken);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

    // Count by role
    long countByRole(User.Role role);

    // Find all users by role
    List<User> findByRole(User.Role role);

    // Find pending instructor requests
    List<User> findByInstructorStatus(User.InstructorStatus status);

    // Search users by name or email
    List<User> findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String name, String email
    );
}