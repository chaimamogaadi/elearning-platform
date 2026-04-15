import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { TokenService } from '../../services/token';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class HomeComponent implements OnInit {

  private http         = inject(HttpClient);
  private tokenService = inject(TokenService);

  isLoggedIn = false;
  user: any  = null;
  isLoading  = true;

  featuredCourses: any[] = [];
  instructors: any[]     = [];
  categoryList: {name: string; count: number}[] = [];
  stats: any = {
    totalCourses:      0,
    totalStudents:     0,
    totalInstructors:  0,
    totalEnrollments:  0
  };

  currentYear = new Date().getFullYear();

  categoryIcons: Record<string, string> = {
    'Programming':  '💻',
    'Design':       '🎨',
    'Business':     '💼',
    'Marketing':    '📈',
    'Data Science': '📊',
    'Language':     '🌍',
    'Music':        '🎵',
    'Photography':  '📷',
    'Other':        '📚',
  };

  getCategoryIcon(name: string): string {
    return this.categoryIcons[name] || '📚';
  }

  getInitials(name: string): string {
    if (!name) return '?';
    return name.split(' ')
      .map((n: string) => n[0])
      .join('')
      .toUpperCase()
      .slice(0, 2);
  }

  ngOnInit(): void {
    this.isLoggedIn = this.tokenService.isLoggedIn();
    if (this.isLoggedIn) {
      this.user = this.tokenService.getUser();
    }

    this.http.get<any>(
      'http://localhost:8080/api/home/data'
    ).subscribe({
      next: (data) => {
        this.featuredCourses = data.featuredCourses || [];
        this.instructors     = data.instructors     || [];
        this.stats           = data.stats           || this.stats;
        this.isLoading       = false;

        // Convert category map to sorted array
        const counts: Record<string, number> =
          data.categoryCounts || {};
        this.categoryList = Object.entries(counts)
          .map(([name, count]) => ({
            name,
            count: count as number
          }))
          .sort((a, b) => b.count - a.count)
          .slice(0, 8);
      },
      error: (err) => {
        console.error('Home data error:', err);
        this.isLoading = false;
      }
    });
  }
}