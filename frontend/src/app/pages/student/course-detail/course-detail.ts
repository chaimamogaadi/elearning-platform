import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { StudentService } from '../../../services/student';
import { TokenService } from '../../../services/token';

@Component({
  selector: 'app-course-detail',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './course-detail.html',
  styleUrl: './course-detail.css'
})
export class CourseDetailComponent implements OnInit {

  private route          = inject(ActivatedRoute);
  private router         = inject(Router);
  private studentService = inject(StudentService);
  private tokenService   = inject(TokenService);

  course: any      = null;
  lessons: any[]   = [];
  isLoading        = true;
  isEnrolled       = false;
  enrolling        = false;
  isLoggedIn       = false;
  successMessage   = '';
  errorMessage     = '';
  courseId!: number;

  ngOnInit(): void {
    this.courseId  = Number(
      this.route.snapshot.paramMap.get('id'));
    this.isLoggedIn = this.tokenService.isLoggedIn();
    this.loadCourse();
  }

  loadCourse(): void {
    this.studentService.getCourseDetail(
      this.courseId
    ).subscribe({
      next: (data) => {
        this.course    = data;
        this.isLoading = false;
        this.checkEnrollment();
      },
      error: () => { this.isLoading = false; }
    });
  }

  checkEnrollment(): void {
    if (!this.isLoggedIn) return;
    this.studentService.isEnrolled(
      this.courseId
    ).subscribe({
      next: (res) => { this.isEnrolled = res.enrolled; }
    });
  }

enroll(): void {
  if (!this.isLoggedIn) {
    this.router.navigate(['/login']);
    return;
  }

  // Free course → enroll directly
  if (this.course.price === 0) {
    this.enrolling    = true;
    this.errorMessage = '';

    this.studentService.enroll(this.courseId).subscribe({
      next: (res) => {
        this.enrolling      = false;
        this.isEnrolled     = true;
        this.successMessage = res.message;
      },
      error: (err) => {
        this.enrolling    = false;
        this.errorMessage =
          err.error?.message || 'Enrollment failed.';
      }
    });
  } else {
    // Paid course → go to checkout page
    this.router.navigate(['/checkout', this.courseId]);
  }
}

  startLearning(): void {
    this.router.navigate(
      ['/courses', this.courseId, 'learn']);
  }
}