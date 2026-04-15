import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { InstructorService } from '../../../services/instructor';

@Component({
  selector: 'app-instructor-courses',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './instructor-courses.html',
  styleUrl: './instructor-courses.css'
})
export class InstructorCoursesComponent implements OnInit {

  private instructorService = inject(InstructorService);
  private fb                = inject(FormBuilder);

  courses: any[]  = [];
  isLoading       = true;
  showForm        = false;
  editingCourseId: number | null = null;
  successMessage  = '';
  errorMessage    = '';

  form = this.fb.group({
    title:       ['', Validators.required],
    description: ['', Validators.required],
    price:       [0, Validators.required],
    category:    [''],
    thumbnailUrl:['']
  });

  categories = [
    'Programming', 'Design', 'Business',
    'Marketing', 'Data Science', 'Language',
    'Music', 'Photography', 'Other'
  ];

  ngOnInit(): void { this.loadCourses(); }

  loadCourses(): void {
    this.isLoading = true;
    this.instructorService.getMyCourses().subscribe({
      next: (data: any[]) => { this.courses = data; this.isLoading = false; },
      error: () => { this.isLoading = false; }
    });
  }

  openCreateForm(): void {
    this.showForm       = true;
    this.editingCourseId = null;
    this.form.reset({ price: 0 });
  }

  openEditForm(course: any): void {
    this.showForm        = true;
    this.editingCourseId = course.id;
    this.form.patchValue(course);
  }

  closeForm(): void {
    this.showForm        = false;
    this.editingCourseId = null;
    this.form.reset();
  }

  submit(): void {
    if (this.form.invalid) return;
    this.errorMessage   = '';
    this.successMessage = '';

    const action = this.editingCourseId
      ? this.instructorService.updateCourse(
          this.editingCourseId, this.form.value)
      : this.instructorService.createCourse(this.form.value);

    action.subscribe({
      next: () => {
        this.successMessage = this.editingCourseId
          ? 'Course updated!' : 'Course created! Pending admin approval.';
        this.closeForm();
        this.loadCourses();
      },
      error: (err: { error: { message: string; }; }) => {
        this.errorMessage = err.error?.message || 'Something went wrong.';
      }
    });
  }

  deleteCourse(id: number): void {
    if (!confirm('Delete this course and all its lessons?')) return;
    this.instructorService.deleteCourse(id).subscribe({
      next: () => this.courses = this.courses.filter(c => c.id !== id)
    });
  }
}