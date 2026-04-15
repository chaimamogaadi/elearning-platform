import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { StudentService } from '../../../services/student';

@Component({
  selector: 'app-lesson-viewer',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './lesson-viewer.html',
  styleUrl: './lesson-viewer.css'
})
export class LessonViewerComponent implements OnInit {

  private route          = inject(ActivatedRoute);
  private router         = inject(Router);
  private studentService = inject(StudentService);
  private sanitizer      = inject(DomSanitizer);

  courseId!: number;
  lessons: any[]     = [];
  currentLesson: any = null;
  currentIndex       = 0;
  isLoading          = true;
  errorMessage       = '';

  // Quiz
  quizId: number | null   = null;
  quizTitle: string       = '';
  hasQuiz                 = false;

  ngOnInit(): void {
    this.courseId = Number(
      this.route.snapshot.paramMap.get('id'));
    this.loadLessons();
  }

  loadLessons(): void {
    this.studentService.getCourseLessons(
      this.courseId
    ).subscribe({
      next: (data) => {
        this.lessons   = data;
        this.isLoading = false;

        if (data.length > 0) {
          this.selectLesson(0);
        }

        // Load quizzes after lessons load
        this.loadQuizzes();
      },
      error: (err) => {
        this.isLoading    = false;
        this.errorMessage =
          err.error?.message ||
          'Could not load lessons. Please enroll first.';
      }
    });
  }

  loadQuizzes(): void {
    this.studentService
      .getCourseQuizzes(this.courseId)
      .subscribe({
        next: (quizzes) => {
          console.log('Quizzes loaded:', quizzes);
          if (quizzes && quizzes.length > 0) {
            this.hasQuiz  = true;
            this.quizId   = quizzes[0].id;
            this.quizTitle = quizzes[0].title;
          }
        },
        error: (err) => {
          // Not critical — just means no quiz
          console.log('No quizzes found:', err.status);
          this.hasQuiz = false;
        }
      });
  }

  selectLesson(index: number): void {
    this.currentIndex  = index;
    this.currentLesson = this.lessons[index];
  }

  nextLesson(): void {
    if (this.currentIndex < this.lessons.length - 1) {
      this.selectLesson(this.currentIndex + 1);
    }
  }

  prevLesson(): void {
    if (this.currentIndex > 0) {
      this.selectLesson(this.currentIndex - 1);
    }
  }

  getSafeUrl(url: string): SafeResourceUrl {
    if (url && url.includes('youtube.com/watch')) {
      const videoId = url.split('v=')[1]?.split('&')[0];
      url = `https://www.youtube.com/embed/${videoId}`;
    }
    return this.sanitizer
      .bypassSecurityTrustResourceUrl(url);
  }

  get progressPercent(): number {
    if (!this.lessons.length) return 0;
    return Math.round(
      ((this.currentIndex + 1) /
       this.lessons.length) * 100);
  }

  get isLastLesson(): boolean {
    return this.currentIndex ===
      this.lessons.length - 1;
  }

  goToQuiz(): void {
    if (this.quizId) {
      this.router.navigate([
        '/courses', this.courseId,
        'quiz', this.quizId
      ]);
    }
  }
}