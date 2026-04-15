import { Component, inject, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { StudentService } from '../../../services/student';

@Component({
  selector: 'app-quiz-player',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './quiz-player.html',
  styleUrl: './quiz-player.css'
})
export class QuizPlayerComponent implements OnInit, OnDestroy {

  private route          = inject(ActivatedRoute);
  private router         = inject(Router);
  private studentService = inject(StudentService);

  courseId!: number;
  quizId!: number;
  quiz: any           = null;
  questions: any[]    = [];
  isLoading           = true;

  // Quiz state
  started             = false;
  finished            = false;
  currentIndex        = 0;
  answers: Record<number, string> = {};
  result: any         = null;
  isSubmitting        = false;
  errorMessage        = '';

  // Timer
  timeLeft            = 0;
  timerInterval: any  = null;

  ngOnInit(): void {
    this.courseId = Number(
      this.route.snapshot.paramMap.get('id'));
    this.quizId   = Number(
      this.route.snapshot.paramMap.get('quizId'));
    this.loadQuiz();
  }

  ngOnDestroy(): void {
    this.clearTimer();
  }

  loadQuiz(): void {
    this.studentService
      .getCourseQuizzes(this.courseId)
      .subscribe({
        next: (quizzes) => {
          console.log('All quizzes:', quizzes);

          // Find the specific quiz by ID
          this.quiz = quizzes.find(
            (q: any) => q.id === this.quizId);

          if (this.quiz) {
            this.questions =
              this.quiz.questions || [];
            this.timeLeft  =
              (this.quiz.timeLimitMinutes || 30) * 60;
            console.log('Quiz loaded:',
              this.quiz.title,
              'Questions:', this.questions.length);
          } else {
            this.errorMessage = 'Quiz not found';
          }

          this.isLoading = false;
        },
        error: (err) => {
          console.error('Quiz load error:', err);
          this.isLoading    = false;
          this.errorMessage =
            'Could not load quiz. ' +
            'Please make sure you are enrolled.';
        }
      });
  }
  startQuiz(): void {
    this.started = true;
    this.startTimer();
  }

  startTimer(): void {
    this.timerInterval = setInterval(() => {
      this.timeLeft--;
      if (this.timeLeft <= 0) {
        this.clearTimer();
        this.submitQuiz(); // auto-submit when time runs out
      }
    }, 1000);
  }

  clearTimer(): void {
    if (this.timerInterval) {
      clearInterval(this.timerInterval);
    }
  }

  get formattedTime(): string {
    const m = Math.floor(this.timeLeft / 60)
      .toString().padStart(2, '0');
    const s = (this.timeLeft % 60)
      .toString().padStart(2, '0');
    return `${m}:${s}`;
  }

  get currentQuestion(): any {
    return this.questions[this.currentIndex];
  }

  get progressPercent(): number {
    return Math.round(
      ((this.currentIndex + 1) /
       this.questions.length) * 100);
  }

  get answeredCount(): number {
    return Object.keys(this.answers).length;
  }

  getOptions(question: any): string[] {
    if (!question.options) return [];

    // Handle both array and comma-separated string
    if (Array.isArray(question.options)) {
      return question.options;
    }

    return question.options
      .split(',')
      .map((o: string) => o.trim())
      .filter((o: string) => o.length > 0);
  }

  selectAnswer(questionId: number,
               answer: string): void {
    this.answers[questionId] = answer;
  }

  isSelected(questionId: number,
             answer: string): boolean {
    return this.answers[questionId] === answer;
  }

  nextQuestion(): void {
    if (this.currentIndex < this.questions.length - 1) {
      this.currentIndex++;
    }
  }

  prevQuestion(): void {
    if (this.currentIndex > 0) {
      this.currentIndex--;
    }
  }

  goToQuestion(index: number): void {
    this.currentIndex = index;
  }

  submitQuiz(): void {
    this.clearTimer();
    this.isSubmitting = true;
    this.errorMessage = '';

    this.studentService.submitQuiz(
      this.quizId, this.answers
    ).subscribe({
      next: (res) => {
        this.result      = res;
        this.finished    = true;
        this.isSubmitting = false;
      },
      error: (err) => {
        this.isSubmitting = false;
        this.errorMessage =
          err.error?.message || 'Submission failed';
      }
    });
  }

  retakeQuiz(): void {
    this.finished     = false;
    this.started      = false;
    this.answers      = {};
    this.currentIndex = 0;
    this.result       = null;
    this.timeLeft     =
      (this.quiz?.timeLimitMinutes || 30) * 60;
  }
}