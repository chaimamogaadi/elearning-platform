import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { StudentService } from '../../../services/student';
import { TokenService } from '../../../services/token';

@Component({
  selector: 'app-course-catalog',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './course-catalog.html',
  styleUrl: './course-catalog.css'
})
export class CourseCatalogComponent implements OnInit {

  private studentService = inject(StudentService);
  private tokenService   = inject(TokenService);

  courses: any[]     = [];
  isLoading          = true;
  searchKeyword      = '';
  selectedCategory   = '';
  isLoggedIn         = false;

  categories = [
    'All', 'Programming', 'Design', 'Business',
    'Marketing', 'Data Science', 'Language',
    'Music', 'Photography', 'Other'
  ];

  ngOnInit(): void {
    this.isLoggedIn = this.tokenService.isLoggedIn();
    this.loadCourses();
  }

  loadCourses(): void {
    this.isLoading = true;
    const cat = this.selectedCategory === 'All'
      ? '' : this.selectedCategory;

    this.studentService.browseCourses(
      this.searchKeyword, cat
    ).subscribe({
      next: (data) => {
        this.courses   = data;
        this.isLoading = false;
      },
      error: () => { this.isLoading = false; }
    });
  }

  search(): void { this.loadCourses(); }

  filterByCategory(cat: string): void {
    this.selectedCategory = cat;
    this.loadCourses();
  }

  getInitials(name: string): string {
    return name ? name.charAt(0).toUpperCase() : '?';
  }
}