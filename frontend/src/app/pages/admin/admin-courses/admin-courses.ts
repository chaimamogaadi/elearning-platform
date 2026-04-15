import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminService } from '../../../services/admin';

@Component({
  selector: 'app-admin-courses',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './admin-courses.html',
  styleUrl: './admin-courses.css'
})
export class AdminCoursesComponent implements OnInit {

  private adminService = inject(AdminService);

  courses: any[] = [];
  isLoading = true;

  ngOnInit(): void {
    this.loadCourses();
  }

  loadCourses(): void {
    this.adminService.getCourses().subscribe({
      next: (data) => { this.courses = data; this.isLoading = false; },
      error: () => { this.isLoading = false; }
    });
  }

  approve(id: number): void {
    this.adminService.approveCourse(id).subscribe({
      next: (updated) => {
        const i = this.courses.findIndex(c => c.id === id);
        if (i !== -1) this.courses[i] = updated;
      }
    });
  }

  reject(id: number): void {
    this.adminService.rejectCourse(id).subscribe({
      next: (updated) => {
        const i = this.courses.findIndex(c => c.id === id);
        if (i !== -1) this.courses[i] = updated;
      }
    });
  }

  delete(id: number): void {
    if (!confirm('Delete this course permanently?')) return;
    this.adminService.deleteCourse(id).subscribe({
      next: () => this.courses = this.courses.filter(c => c.id !== id)
    });
  }

  getStatusClass(status: string): string {
    return {
      'APPROVED': 'status-approved',
      'PENDING':  'status-pending',
      'REJECTED': 'status-rejected',
      'DRAFT':    'status-draft'
    }[status] || 'status-draft';
  }
}