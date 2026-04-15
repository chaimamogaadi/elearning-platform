import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminService } from '../../../services/admin';

@Component({
  selector: 'app-admin-overview',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './admin-overview.html',
  styleUrl: './admin-overview.css'
})
export class AdminOverviewComponent implements OnInit {

  private adminService = inject(AdminService);

  stats: any = null;
  pending: any[] = [];
  isLoading = true;

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.adminService.getStats().subscribe({
      next: (data) => { this.stats = data; this.isLoading = false; },
      error: () => { this.isLoading = false; }
    });

    this.adminService.getPendingInstructors().subscribe({
      next: (data) => this.pending = data
    });
  }

  approve(id: number): void {
    this.adminService.approveInstructor(id).subscribe({
      next: () => {
        this.pending = this.pending.filter(u => u.id !== id);
        if (this.stats) this.stats.pendingInstructorRequests--;
      }
    });
  }

  reject(id: number): void {
    this.adminService.rejectInstructor(id).subscribe({
      next: () => {
        this.pending = this.pending.filter(u => u.id !== id);
        if (this.stats) this.stats.pendingInstructorRequests--;
      }
    });
  }
}