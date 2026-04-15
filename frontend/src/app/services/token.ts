import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class TokenService {

  private TOKEN_KEY = 'auth_token';
  private USER_KEY  = 'auth_user';

  // Save JWT token after login
  saveToken(token: string): void {
    localStorage.setItem(this.TOKEN_KEY, token);
  }

  // Get token to attach to API requests
  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  // Save user info (username, email, role)
  saveUser(user: any): void {
    localStorage.setItem(this.USER_KEY, JSON.stringify(user));
  }

  // Get saved user info
  getUser(): any {
    const user = localStorage.getItem(this.USER_KEY);
    return user ? JSON.parse(user) : null;
  }

  // Returns true if user is logged in
  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  // Clear everything when logging out
  clearAll(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
  }
}