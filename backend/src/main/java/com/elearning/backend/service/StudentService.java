package com.elearning.backend.service;

import com.elearning.backend.dto.QuizResultResponse;
import com.elearning.backend.dto.QuizSubmissionRequest;
import com.elearning.backend.model.*;
import com.elearning.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final CourseRepository     courseRepository;
    private final LessonRepository     lessonRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository       userRepository;
    private final QuizRepository       quizRepository;
    private final QuestionRepository   questionRepository;
    private final QuizResultRepository quizResultRepository;

    // ===== BROWSE APPROVED COURSES =====
    public List<Course> browseCourses(String search,
                                      String category) {
        List<Course> all = courseRepository
                .findByStatus(Course.CourseStatus.APPROVED);

        if (search != null && !search.isBlank()) {
            String kw = search.toLowerCase();
            all = all.stream()
                    .filter(c -> c.getTitle()
                            .toLowerCase().contains(kw) ||
                            (c.getDescription() != null &&
                                    c.getDescription()
                                            .toLowerCase().contains(kw)))
                    .collect(Collectors.toList());
        }

        if (category != null && !category.isBlank()) {
            all = all.stream()
                    .filter(c -> category.equalsIgnoreCase(
                            c.getCategory()))
                    .collect(Collectors.toList());
        }

        return all;
    }

    // ===== COURSE DETAIL =====
    public Course getCourseDetail(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() ->
                        new RuntimeException("Course not found"));
    }

    // ===== GET LESSONS (only if enrolled or free) =====
    public List<Lesson> getCourseLessons(Long courseId,
                                         String studentEmail) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() ->
                        new RuntimeException("Course not found"));

        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        // Free course OR enrolled → show all lessons
        boolean isFree    = course.getPrice() == 0.0;
        boolean isEnrolled = enrollmentRepository
                .existsByStudentAndCourse(student, course);

        if (!isFree && !isEnrolled) {
            throw new RuntimeException(
                    "Please enroll in this course first");
        }

        return lessonRepository
                .findByCourseOrderByOrderNumAsc(course);
    }

    // ===== ENROLL =====
    public Map<String, String> enroll(Long courseId,
                                      String studentEmail) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() ->
                        new RuntimeException("Course not found"));

        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        if (enrollmentRepository.existsByStudentAndCourse(
                student, course)) {
            throw new RuntimeException(
                    "You are already enrolled in this course");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollmentRepository.save(enrollment);

        return Map.of("message",
                "Successfully enrolled in " + course.getTitle());
    }

    // ===== MY ENROLLED COURSES =====
    public List<Course> getMyCourses(String studentEmail) {
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        return enrollmentRepository.findByStudent(student)
                .stream()
                .map(Enrollment::getCourse)
                .collect(Collectors.toList());
    }

    // ===== CHECK ENROLLMENT =====
    public boolean isEnrolled(Long courseId,
                              String studentEmail) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() ->
                        new RuntimeException("Course not found"));

        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        return enrollmentRepository
                .existsByStudentAndCourse(student, course);
    }
    // ===== GET QUIZZES FOR A COURSE =====
    public List<Quiz> getCourseQuizzes(Long courseId,
                                       String studentEmail) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() ->
                        new RuntimeException("Course not found"));

        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() ->
                        new RuntimeException("Student not found"));

        if (!enrollmentRepository.existsByStudentAndCourse(
                student, course)) {
            throw new RuntimeException(
                    "Please enroll in this course first");
        }

        return quizRepository.findByCourse(course);
    }

    // ===== SUBMIT QUIZ =====
    public QuizResultResponse submitQuiz(
            QuizSubmissionRequest request,
            String studentEmail) {

        Quiz quiz = quizRepository.findById(request.getQuizId())
                .orElseThrow(() ->
                        new RuntimeException("Quiz not found"));

        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() ->
                        new RuntimeException("Student not found"));

        // Get all questions for this quiz
        List<Question> questions =
                questionRepository.findByQuiz(quiz);

        int totalPoints = 0;
        int earnedPoints = 0;
        List<QuizResultResponse.QuestionResultDto> results =
                new java.util.ArrayList<>();

        // Grade each answer
        for (Question q : questions) {
            totalPoints += q.getPoints();
            String studentAnswer =
                    request.getAnswers().get(q.getId());
            boolean isCorrect = q.getCorrectAnswer()
                    .equals(studentAnswer);

            if (isCorrect) earnedPoints += q.getPoints();

            results.add(new QuizResultResponse.QuestionResultDto(
                    q.getId(),
                    q.getQuestionText(),
                    studentAnswer,
                    q.getCorrectAnswer(),
                    isCorrect,
                    q.getPoints()
            ));
        }

        int percentage = totalPoints > 0
                ? (earnedPoints * 100) / totalPoints : 0;
        boolean passed = percentage >= quiz.getPassingScore();

        // Save result
        QuizResult result = new QuizResult();
        result.setStudent(student);
        result.setQuiz(quiz);
        result.setScore(earnedPoints);
        result.setTotalPoints(totalPoints);
        result.setPercentage(percentage);
        result.setPassed(passed);
        quizResultRepository.save(result);

        String message = passed
                ? "🎉 Congratulations! You passed with " +
                percentage + "%!"
                : "📚 You scored " + percentage +
                "%. You need " + quiz.getPassingScore() +
                "% to pass. Try again!";

        return new QuizResultResponse(
                earnedPoints, totalPoints,
                percentage, passed, message, results
        );
    }
}