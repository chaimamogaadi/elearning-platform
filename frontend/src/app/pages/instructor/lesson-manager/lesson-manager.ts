import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { InstructorService } from '../../../services/instructor';

@Component({
  selector: 'app-lesson-manager',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './lesson-manager.html',
  styleUrl: './lesson-manager.css'
})
export class LessonManagerComponent implements OnInit {

  private route             = inject(ActivatedRoute);
  private instructorService = inject(InstructorService);
  private fb                = inject(FormBuilder);

  courseId!: number;
  lessons: any[]  = [];
  showForm        = false;
  isLoading       = true;
  successMessage  = '';

  form = this.fb.group({
    title:           ['', Validators.required],
    content:         [''],
    videoUrl:        [''],
    type:            ['TEXT'],
    orderNum:        [1],
    durationMinutes: [0]
  });

  ngOnInit(): void {
    this.courseId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadLessons();
  }

  loadLessons(): void {
    this.isLoading = true;
    this.instructorService.getLessons(this.courseId).subscribe({
      next: (data: any[]) => { this.lessons = data; this.isLoading = false; },
      error: () => { this.isLoading = false; }
    });
  }

  addLesson(): void {
    if (this.form.invalid) return;

    this.instructorService.addLesson(
      this.courseId, this.form.value
    ).subscribe({
      next: () => {
        this.successMessage = 'Lesson added successfully!';
        this.showForm       = false;
        this.form.reset({ type: 'TEXT', orderNum: 1, durationMinutes: 0 });
        this.loadLessons();
        setTimeout(() => this.successMessage = '', 3000);
      }
    });
  }

  deleteLesson(id: number): void {
    if (!confirm('Delete this lesson?')) return;
    this.instructorService.deleteLesson(id).subscribe({
      next: () => this.lessons = this.lessons.filter(l => l.id !== id)
    });
  }
}