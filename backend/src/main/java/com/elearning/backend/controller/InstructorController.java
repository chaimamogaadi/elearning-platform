package com.elearning.backend.controller;

import com.elearning.backend.dto.*;
import com.elearning.backend.model.*;
import com.elearning.backend.service.InstructorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/instructor")
@RequiredArgsConstructor
public class InstructorController {

    private final InstructorService instructorService;

    // Helper: get logged-in user's email from JWT
    private String getEmail(Authentication auth) {
        return auth.getName();
    }

    // ===== COURSES =====

    @PostMapping("/courses")
    public ResponseEntity<Course> createCourse(
            @RequestBody CourseRequest req,
            Authentication auth) {
        return ResponseEntity.ok(
                instructorService.createCourse(req, getEmail(auth)));
    }

    @GetMapping("/courses")
    public ResponseEntity<List<Course>> getMyCourses(Authentication auth) {
        return ResponseEntity.ok(
                instructorService.getMyCourses(getEmail(auth)));
    }

    @PutMapping("/courses/{id}")
    public ResponseEntity<Course> updateCourse(
            @PathVariable Long id,
            @RequestBody CourseRequest req,
            Authentication auth) {
        return ResponseEntity.ok(
                instructorService.updateCourse(id, req, getEmail(auth)));
    }

    @DeleteMapping("/courses/{id}")
    public ResponseEntity<Map<String, String>> deleteCourse(
            @PathVariable Long id,
            Authentication auth) {
        instructorService.deleteCourse(id, getEmail(auth));
        return ResponseEntity.ok(Map.of("message", "Course deleted"));
    }

    // ===== LESSONS =====

    @PostMapping("/courses/{courseId}/lessons")
    public ResponseEntity<Lesson> addLesson(
            @PathVariable Long courseId,
            @RequestBody LessonRequest req,
            Authentication auth) {
        return ResponseEntity.ok(
                instructorService.addLesson(courseId, req, getEmail(auth)));
    }

    @GetMapping("/courses/{courseId}/lessons")
    public ResponseEntity<List<Lesson>> getLessons(
            @PathVariable Long courseId,
            Authentication auth) {
        return ResponseEntity.ok(
                instructorService.getCourseLessons(courseId, getEmail(auth)));
    }

    @DeleteMapping("/lessons/{lessonId}")
    public ResponseEntity<Map<String, String>> deleteLesson(
            @PathVariable Long lessonId,
            Authentication auth) {
        instructorService.deleteLesson(lessonId, getEmail(auth));
        return ResponseEntity.ok(Map.of("message", "Lesson deleted"));
    }

    // ===== QUIZZES =====

    @PostMapping("/courses/{courseId}/quizzes")
    public ResponseEntity<Quiz> createQuiz(
            @PathVariable Long courseId,
            @RequestBody QuizRequest req,
            Authentication auth) {
        return ResponseEntity.ok(
                instructorService.createQuiz(courseId, req, getEmail(auth)));
    }

    @GetMapping("/courses/{courseId}/quizzes")
    public ResponseEntity<List<Quiz>> getQuizzes(
            @PathVariable Long courseId,
            Authentication auth) {
        return ResponseEntity.ok(
                instructorService.getCourseQuizzes(courseId, getEmail(auth)));
    }

    // ===== STUDENTS =====

    @GetMapping("/courses/{courseId}/students")
    public ResponseEntity<List<User>> getStudents(
            @PathVariable Long courseId,
            Authentication auth) {
        return ResponseEntity.ok(
                instructorService.getEnrolledStudents(courseId, getEmail(auth)));
    }
}