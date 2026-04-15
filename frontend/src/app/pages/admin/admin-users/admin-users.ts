import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminService } from '../../../services/admin';

@Component({
  selector: 'app-admin-users',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-users.html',
  styleUrl: './admin-users.css'
})
export class AdminUsersComponent implements OnInit {

  private adminService = inject(AdminService);

  users: any[]     = [];
  isLoading        = true;
  searchKeyword    = '';
  confirmDeleteId: number | null = null;

  ngOnInit(): void { this.loadUsers(); }

  loadUsers(): void {
    this.isLoading = true;
    this.adminService.getUsers().subscribe({
      next: (data) => { this.users = data; this.isLoading = false; },
      error: () => { this.isLoading = false; }
    });
  }

  search(): void {
    if (!this.searchKeyword.trim()) { this.loadUsers(); return; }
    this.adminService.getUsers(this.searchKeyword).subscribe({
      next: (data) => this.users = data
    });
  }

  toggleStatus(user: any): void {
    this.adminService.toggleUserStatus(user.id).subscribe({
      next: (updated) => {
        const i = this.users.findIndex(u => u.id === user.id);
        if (i !== -1) this.users[i] = updated;
      }
    });
  }

  confirmDelete(id: number): void { this.confirmDeleteId = id; }

  deleteUser(): void {
    if (!this.confirmDeleteId) return;
    this.adminService.deleteUser(this.confirmDeleteId).subscribe({
      next: () => {
        this.users = this.users.filter(u => u.id !== this.confirmDeleteId);
        this.confirmDeleteId = null;
      }
    });
  }

  getRoleBadgeClass(role: string): string {
    return {
      'ADMIN': 'badge-admin',
      'INSTRUCTOR': 'badge-instructor',
      'STUDENT': 'badge-student'
    }[role] || 'badge-student';
  }
}