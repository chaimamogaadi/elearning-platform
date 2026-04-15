import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TokenService } from './token';
import { Router } from '@angular/router';

@Injectable({ providedIn: 'root' })
export class AuthService {

  // Your Spring Boot backend URL
  private apiUrl = 'http://localhost:8080/api/auth';

  // Angular 18+ way — inject() instead of constructor
  private http         = inject(HttpClient);
  private tokenService = inject(TokenService);
  private router       = inject(Router);

  // Call POST /api/auth/register
  register(data: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, data);
  }

  // Call POST /api/auth/login
  login(data: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/login`, data);
  }

  // Call POST /api/auth/forgot-password
  forgotPassword(email: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/forgot-password`, { email });
  }

  resetPassword(token: string, newPassword: string): Observable<any> {
    return this.http.post<any>(
      `${this.apiUrl}/reset-password`,
      { token, newPassword }
    );
  }

  // Save token + user info after login or register
  handleLoginSuccess(response: any): void {
    this.tokenService.saveToken(response.token);
    this.tokenService.saveUser({
      username: response.username,
      email:    response.email,
      role:     response.role
    });
  }

  // Clear data and go to login page
  logout(): void {
    this.tokenService.clearAll();
    this.router.navigate(['/login']);
  }

  isLoggedIn(): boolean {
    return this.tokenService.isLoggedIn();
  }
}