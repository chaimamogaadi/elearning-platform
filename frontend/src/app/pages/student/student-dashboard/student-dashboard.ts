import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../services/auth';
import { StudentService } from '../../../services/student';

@Component({
  selector: 'app-student-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './student-dashboard.html',
  styleUrl: './student-dashboard.css'
})
export class StudentDashboardComponent implements OnInit {

  private authService    = inject(AuthService);
  private studentService = inject(StudentService);

  user: any            = null;
  enrolledCourses: any[] = [];
  isLoading            = true;
  isCoursesLoading     = true;
  requestSent          = false;
  requestError         = '';
  requestSuccess       = '';
  sendingRequest       = false;

  ngOnInit(): void {
    // Load user profile
    this.studentService.getMe().subscribe({
      next: (data) => {
        this.user      = data;
        this.isLoading = false;

        if (data.instructorStatus === 'PENDING') {
          this.requestSent = true;
        }
      },
      error: (err) => {
        console.error('Failed to load user:', err);
        this.isLoading = false;
      }
    });

    // Load enrolled courses separately
    this.studentService.getMyCourses().subscribe({
      next: (data) => {
        console.log('Enrolled courses:', data); // debug
        this.enrolledCourses    = data;
        this.isCoursesLoading   = false;
      },
      error: (err) => {
        console.error('Failed to load courses:', err);
        this.isCoursesLoading = false;
      }
    });
  }

  requestInstructor(): void {
    this.sendingRequest = true;
    this.requestError   = '';
    this.requestSuccess = '';

    this.studentService.requestInstructor().subscribe({
      next: (res) => {
        this.sendingRequest = false;
        this.requestSent    = true;
        this.requestSuccess = res.message;
      },
      error: (err) => {
        this.sendingRequest = false;
        this.requestError   =
          err.error?.message ||
          'Something went wrong. Try again.';
      }
    });
  }

  logout(): void {
    this.authService.logout();
  }
}