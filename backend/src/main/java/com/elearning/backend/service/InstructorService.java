package com.elearning.backend.service;

import com.elearning.backend.dto.*;
import com.elearning.backend.model.*;
import com.elearning.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InstructorService {

    private final CourseRepository     courseRepository;
    private final LessonRepository     lessonRepository;
    private final QuizRepository       quizRepository;
    private final QuestionRepository   questionRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository       userRepository;

    // ========== COURSES ==========

    public Course createCourse(CourseRequest req, String instructorEmail) {
        User instructor = userRepository.findByEmail(instructorEmail)
                .orElseThrow(() -> new RuntimeException("Instructor not found"));

        Course course = new Course();
        course.setTitle(req.getTitle());
        course.setDescription(req.getDescription());
        course.setPrice(req.getPrice() != null ? req.getPrice() : 0.0);
        course.setCategory(req.getCategory());
        course.setThumbnailUrl(req.getThumbnailUrl());
        course.setInstructor(instructor);
        course.setStatus(Course.CourseStatus.PENDING); // needs admin approval

        return courseRepository.save(course);
    }

    public List<Course> getMyCourses(String instructorEmail) {
        User instructor = userRepository.findByEmail(instructorEmail)
                .orElseThrow(() -> new RuntimeException("Instructor not found"));
        return courseRepository.findByInstructor(instructor);
    }

    public Course updateCourse(Long courseId, CourseRequest req,
                               String instructorEmail) {
        Course course = getCourseAndVerifyOwner(courseId, instructorEmail);
        course.setTitle(req.getTitle());
        course.setDescription(req.getDescription());
        course.setPrice(req.getPrice());
        course.setCategory(req.getCategory());
        course.setThumbnailUrl(req.getThumbnailUrl());
        return courseRepository.save(course);
    }

    public void deleteCourse(Long courseId, String instructorEmail) {
        Course course = getCourseAndVerifyOwner(courseId, instructorEmail);
        courseRepository.delete(course);
    }

    // ========== LESSONS ==========

    public Lesson addLesson(Long courseId, LessonRequest req,
                            String instructorEmail) {
        Course course = getCourseAndVerifyOwner(courseId, instructorEmail);

        Lesson lesson = new Lesson();
        lesson.setCourse(course);
        lesson.setTitle(req.getTitle());
        lesson.setContent(req.getContent());
        lesson.setVideoUrl(req.getVideoUrl());
        lesson.setOrderNum(req.getOrderNum() != null ? req.getOrderNum() : 0);
        lesson.setDurationMinutes(
                req.getDurationMinutes() != null ? req.getDurationMinutes() : 0);
        lesson.setType(req.getType() != null
                ? Lesson.LessonType.valueOf(req.getType())
                : Lesson.LessonType.TEXT);

        Lesson saved = lessonRepository.save(lesson);

        // Update total lessons count on course
        course.setTotalLessons((int) lessonRepository.countByCourse(course));
        courseRepository.save(course);

        return saved;
    }

    public List<Lesson> getCourseLessons(Long courseId,
                                         String instructorEmail) {
        Course course = getCourseAndVerifyOwner(courseId, instructorEmail);
        return lessonRepository.findByCourseOrderByOrderNumAsc(course);
    }

    public void deleteLesson(Long lessonId, String instructorEmail) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));

        // Verify ownership via course
        getCourseAndVerifyOwner(
                lesson.getCourse().getId(), instructorEmail);

        Course course = lesson.getCourse();
        lessonRepository.delete(lesson);

        // Update count
        course.setTotalLessons((int) lessonRepository.countByCourse(course));
        courseRepository.save(course);
    }

    // ========== QUIZZES ==========

    public Quiz createQuiz(Long courseId, QuizRequest req,
                           String instructorEmail) {
        Course course = getCourseAndVerifyOwner(courseId, instructorEmail);

        Quiz quiz = new Quiz();
        quiz.setCourse(course);
        quiz.setTitle(req.getTitle());
        quiz.setDescription(req.getDescription());
        quiz.setPassingScore(
                req.getPassingScore() != null ? req.getPassingScore() : 70);
        quiz.setTimeLimitMinutes(
                req.getTimeLimitMinutes() != null ? req.getTimeLimitMinutes() : 30);

        Quiz savedQuiz = quizRepository.save(quiz);

        // Save questions
        if (req.getQuestions() != null) {
            for (QuestionRequest qReq : req.getQuestions()) {
                Question q = new Question();
                q.setQuiz(savedQuiz);
                q.setQuestionText(qReq.getQuestionText());
                q.setOptions(String.join(",", qReq.getOptions()));
                q.setCorrectAnswer(qReq.getCorrectAnswer());
                q.setPoints(qReq.getPoints() != null ? qReq.getPoints() : 1);
                questionRepository.save(q);
            }
        }

        return savedQuiz;
    }

    public List<Quiz> getCourseQuizzes(Long courseId,
                                       String instructorEmail) {
        Course course = getCourseAndVerifyOwner(courseId, instructorEmail);
        return quizRepository.findByCourse(course);
    }

    // ========== STUDENTS ==========

    public List<User> getEnrolledStudents(Long courseId,
                                          String instructorEmail) {
        Course course = getCourseAndVerifyOwner(courseId, instructorEmail);
        return enrollmentRepository.findByCourse(course)
                .stream()
                .map(Enrollment::getStudent)
                .collect(Collectors.toList());
    }

    // ========== HELPER ==========

    private Course getCourseAndVerifyOwner(Long courseId,
                                           String instructorEmail) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (!course.getInstructor().getEmail().equals(instructorEmail)) {
            throw new RuntimeException("Access denied. Not your course.");
        }

        return course;
    }
}