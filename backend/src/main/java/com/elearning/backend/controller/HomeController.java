package com.elearning.backend.controller;

import com.elearning.backend.model.Course;
import com.elearning.backend.model.User;
import com.elearning.backend.repository.CourseRepository;
import com.elearning.backend.repository.EnrollmentRepository;
import com.elearning.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeController {

    private final CourseRepository     courseRepository;
    private final UserRepository       userRepository;
    private final EnrollmentRepository enrollmentRepository;

    // GET /api/home/data
    // Returns everything the home page needs in one call
    @GetMapping("/data")
    public ResponseEntity<Map<String, Object>> getHomeData() {

        // Get approved courses
        List<Course> approved = courseRepository
                .findByStatus(Course.CourseStatus.APPROVED);

        // Featured courses — latest 6
        List<Course> featured = approved.stream()
                .sorted(Comparator.comparing(
                        Course::getCreatedAt).reversed())
                .limit(6)
                .collect(Collectors.toList());

        // Categories with course count
        Map<String, Long> categoryCounts = approved
                .stream()
                .filter(c -> c.getCategory() != null
                        && !c.getCategory().isBlank())
                .collect(Collectors.groupingBy(
                        Course::getCategory,
                        Collectors.counting()));

        // Instructors — users with INSTRUCTOR role
        List<User> instructors = userRepository
                .findByRole(User.Role.INSTRUCTOR);

        // Platform stats
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCourses",   approved.size());
        stats.put("totalStudents",
                userRepository.countByRole(
                        User.Role.STUDENT));
        stats.put("totalInstructors",
                instructors.size());
        stats.put("totalEnrollments",
                enrollmentRepository.count());

        // Build response
        Map<String, Object> response = new HashMap<>();
        response.put("featuredCourses",  featured);
        response.put("categoryCounts",   categoryCounts);
        response.put("instructors",      instructors);
        response.put("stats",            stats);

        return ResponseEntity.ok(response);
    }
}