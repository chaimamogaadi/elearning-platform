import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { StudentService } from '../../../services/student';

@Component({
  selector: 'app-my-courses',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './my-courses.html',
  styleUrl: './my-courses.css'
})
export class MyCoursesComponent implements OnInit {

  private studentService = inject(StudentService);

  courses: any[] = [];
  isLoading      = true;

  ngOnInit(): void {
    this.studentService.getMyCourses().subscribe({
      next: (data) => {
        this.courses   = data;
        this.isLoading = false;
      },
      error: () => { this.isLoading = false; }
    });
  }
}