package com.elearning.backend.repository;

import com.elearning.backend.model.Certificate;
import com.elearning.backend.model.User;
import com.elearning.backend.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CertificateRepository
        extends JpaRepository<Certificate, Long> {

    // Check if certificate already exists
    Optional<Certificate> findByStudentAndCourse(
            User student, Course course);

    // Get all certificates for a student
    List<Certificate> findByStudent(User student);

    // Find by certificate number (for verification)
    Optional<Certificate> findByCertificateNumber(
            String certificateNumber);
}