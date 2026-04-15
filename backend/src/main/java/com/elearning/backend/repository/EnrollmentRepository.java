package com.elearning.backend.repository;

import com.elearning.backend.model.Enrollment;
import com.elearning.backend.model.Course;
import com.elearning.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    long count();
    List<Enrollment> findByCourse(Course course);
    List<Enrollment> findByStudent(User student);
    boolean existsByStudentAndCourse(User student, Course course);
}