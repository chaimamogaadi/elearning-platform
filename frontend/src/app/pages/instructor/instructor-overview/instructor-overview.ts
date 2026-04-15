import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { InstructorService } from '../../../services/instructor';

@Component({
  selector: 'app-instructor-overview',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './instructor-overview.html',
  styleUrl: './instructor-overview.css'
})
export class InstructorOverviewComponent implements OnInit {

  private instructorService = inject(InstructorService);

  courses: any[] = [];
  totalStudents  = 0;
  totalLessons   = 0;
  isLoading      = true;

  ngOnInit(): void {
    this.instructorService.getMyCourses().subscribe({
      next: (data: any[]) => {
        this.courses      = data;
        this.totalLessons = data.reduce(
          (sum: number, c: any) => sum + (c.totalLessons || 0), 0);
        this.isLoading    = false;
      },
      error: () => { this.isLoading = false; }
    });
  }
}