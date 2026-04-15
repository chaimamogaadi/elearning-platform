import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder,
         FormArray, Validators } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { InstructorService } from '../../../services/instructor';

@Component({
  selector: 'app-quiz-builder',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './quiz-builder.html',
  styleUrl: './quiz-builder.css'
})
export class QuizBuilderComponent implements OnInit {

  private route             = inject(ActivatedRoute);
  private instructorService = inject(InstructorService);
  private fb                = inject(FormBuilder);

  courseId!: number;
  quizzes: any[]  = [];
  showForm        = false;
  isLoading       = true;
  successMessage  = '';
  errorMessage    = '';

  quizForm = this.fb.group({
    title:             ['', Validators.required],
    description:       [''],
    passingScore:      [70, [Validators.required, Validators.min(1),
                             Validators.max(100)]],
    timeLimitMinutes:  [30, Validators.required],
    questions:         this.fb.array([])
  });

  get questions(): FormArray {
    return this.quizForm.get('questions') as FormArray;
  }

  newQuestion() {
    return this.fb.group({
      questionText:  ['', Validators.required],
      options:       this.fb.array([
        this.fb.control('', Validators.required),
        this.fb.control('', Validators.required),
        this.fb.control('', Validators.required),
        this.fb.control('', Validators.required),
      ]),
      correctAnswer: ['', Validators.required],
      points:        [1]
    });
  }

  getOptions(questionIndex: number): FormArray {
    return this.questions.at(questionIndex)
      .get('options') as FormArray;
  }

  ngOnInit(): void {
    this.courseId = Number(
      this.route.snapshot.paramMap.get('id'));
    this.loadQuizzes();
  }

  loadQuizzes(): void {
    this.isLoading = true;
    this.instructorService.getQuizzes(this.courseId).subscribe({
      next: (data) => { this.quizzes = data; this.isLoading = false; },
      error: () => { this.isLoading = false; }
    });
  }

  addQuestion(): void {
    this.questions.push(this.newQuestion());
  }

  removeQuestion(index: number): void {
    this.questions.removeAt(index);
  }

  submitQuiz(): void {
    if (this.quizForm.invalid || this.questions.length === 0) {
      this.errorMessage = 'Please add at least one question.';
      return;
    }

    this.errorMessage = '';

    // Build the payload
    const payload = {
      ...this.quizForm.value,
      questions: this.questions.controls.map((q, i) => ({
        questionText:  q.get('questionText')?.value,
        options:       this.getOptions(i).controls
                           .map(o => o.value),
        correctAnswer: q.get('correctAnswer')?.value,
        points:        q.get('points')?.value || 1
      }))
    };

    this.instructorService.createQuiz(
      this.courseId, payload
    ).subscribe({
      next: () => {
        this.successMessage = 'Quiz created successfully!';
        this.showForm       = false;
        this.quizForm.reset({
          passingScore: 70,
          timeLimitMinutes: 30
        });
        // Clear questions array
        while (this.questions.length) {
          this.questions.removeAt(0);
        }
        this.loadQuizzes();
        setTimeout(() => this.successMessage = '', 4000);
      },
      error: (err) => {
        this.errorMessage =
          err.error?.message || 'Failed to create quiz.';
      }
    });
  }

  openForm(): void {
    this.showForm     = true;
    this.errorMessage = '';
    // Add first question automatically
    if (this.questions.length === 0) {
      this.addQuestion();
    }
  }

  closeForm(): void {
    this.showForm = false;
    while (this.questions.length) {
      this.questions.removeAt(0);
    }
    this.quizForm.reset({
      passingScore: 70, timeLimitMinutes: 30
    });
  }
}