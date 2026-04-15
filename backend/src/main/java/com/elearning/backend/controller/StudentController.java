package com.elearning.backend.controller;

import com.elearning.backend.dto.QuizResultResponse;
import com.elearning.backend.dto.QuizSubmissionRequest;
import com.elearning.backend.model.*;
import com.elearning.backend.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    private String getEmail(Authentication auth) {
        return auth.getName();
    }

    // GET /api/student/courses — browse all approved courses
    @GetMapping("/courses")
    public ResponseEntity<List<Course>> browseCourses(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category) {
        return ResponseEntity.ok(
                studentService.browseCourses(search, category));
    }

    // GET /api/student/courses/{id} — course detail
    @GetMapping("/courses/{id}")
    public ResponseEntity<Course> getCourseDetail(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                studentService.getCourseDetail(id));
    }

    // GET /api/student/courses/{id}/lessons — get lessons
    @GetMapping("/courses/{id}/lessons")
    public ResponseEntity<List<Lesson>> getCourseLessons(
            @PathVariable Long id,
            Authentication auth) {
        return ResponseEntity.ok(
                studentService.getCourseLessons(id, getEmail(auth)));
    }

    // POST /api/student/courses/{id}/enroll — enroll
    @PostMapping("/courses/{id}/enroll")
    public ResponseEntity<Map<String, String>> enroll(
            @PathVariable Long id,
            Authentication auth) {
        return ResponseEntity.ok(
                studentService.enroll(id, getEmail(auth)));
    }

    // GET /api/student/my-courses — enrolled courses
    @GetMapping("/my-courses")
    public ResponseEntity<List<Course>> getMyCourses(
            Authentication auth) {
        return ResponseEntity.ok(
                studentService.getMyCourses(getEmail(auth)));
    }

    // GET /api/student/courses/{id}/is-enrolled
    @GetMapping("/courses/{id}/is-enrolled")
    public ResponseEntity<Map<String, Boolean>> isEnrolled(
            @PathVariable Long id,
            Authentication auth) {
        boolean enrolled = studentService.isEnrolled(
                id, getEmail(auth));
        return ResponseEntity.ok(Map.of("enrolled", enrolled));
    }

    // GET /api/student/courses/{id}/quizzes
    @GetMapping("/courses/{id}/quizzes")
    public ResponseEntity<List<Quiz>> getCourseQuizzes(
            @PathVariable Long id,
            Authentication auth) {
        return ResponseEntity.ok(
                studentService.getCourseQuizzes(
                        id, getEmail(auth)));
    }

    // POST /api/student/quiz/submit
    @PostMapping("/quiz/submit")
    public ResponseEntity<QuizResultResponse> submitQuiz(
            @RequestBody QuizSubmissionRequest request,
            Authentication auth) {
        return ResponseEntity.ok(
                studentService.submitQuiz(
                        request, getEmail(auth)));
    }
}