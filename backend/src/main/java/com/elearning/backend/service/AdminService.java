package com.elearning.backend.service;

import com.elearning.backend.dto.AdminStatsResponse;
import com.elearning.backend.dto.UserResponse;
import com.elearning.backend.model.Course;
import com.elearning.backend.model.User;
import com.elearning.backend.repository.CourseRepository;
import com.elearning.backend.repository.EnrollmentRepository;
import com.elearning.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository       userRepository;
    private final CourseRepository     courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    // ===== STATISTICS =====
    public AdminStatsResponse getStats() {
        return new AdminStatsResponse(
                userRepository.count(),
                userRepository.countByRole(User.Role.STUDENT),
                userRepository.countByRole(User.Role.INSTRUCTOR),
                courseRepository.count(),
                enrollmentRepository.count(),
                userRepository.findByInstructorStatus(
                        User.InstructorStatus.PENDING).size()
        );
    }

    // ===== GET ALL USERS =====
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserResponse::fromUser)
                .collect(Collectors.toList());
    }

    // ===== SEARCH USERS =====
    public List<UserResponse> searchUsers(String keyword) {
        return userRepository
                .findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        keyword, keyword)
                .stream()
                .map(UserResponse::fromUser)
                .collect(Collectors.toList());
    }

    // ===== GET PENDING INSTRUCTOR REQUESTS =====
    public List<UserResponse> getPendingInstructors() {
        return userRepository
                .findByInstructorStatus(User.InstructorStatus.PENDING)
                .stream()
                .map(UserResponse::fromUser)
                .collect(Collectors.toList());
    }

    // ===== APPROVE INSTRUCTOR =====
    public UserResponse approveInstructor(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setRole(User.Role.INSTRUCTOR);
        user.setInstructorStatus(User.InstructorStatus.APPROVED);
        return UserResponse.fromUser(userRepository.save(user));
    }

    // ===== REJECT INSTRUCTOR =====
    public UserResponse rejectInstructor(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setInstructorStatus(User.InstructorStatus.REJECTED);
        return UserResponse.fromUser(userRepository.save(user));
    }

    // ===== TOGGLE USER ACTIVE STATUS =====
    public UserResponse toggleUserStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setIsActive(!user.getIsActive());
        return UserResponse.fromUser(userRepository.save(user));
    }

    // ===== DELETE USER =====
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    // ===== GET ALL COURSES =====
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    // ===== APPROVE COURSE =====
    public Course approveCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        course.setStatus(Course.CourseStatus.APPROVED);
        return courseRepository.save(course);
    }

    // ===== REJECT COURSE =====
    public Course rejectCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        course.setStatus(Course.CourseStatus.REJECTED);
        return courseRepository.save(course);
    }

    // ===== DELETE COURSE =====
    public void deleteCourse(Long courseId) {
        courseRepository.deleteById(courseId);
    }
}