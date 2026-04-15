import { Component, inject } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../services/auth';
import { TokenService } from '../../../services/token';

@Component({
  selector: 'app-admin-layout',
  standalone: true,
  imports: [
    RouterOutlet,        // ✅ renders child pages inside sidebar
    RouterLink,
    RouterLinkActive,
    CommonModule
  ],
  templateUrl: './admin-layout.html',
  styleUrl: './admin-layout.css'
})
export class AdminLayoutComponent {
  private authService  = inject(AuthService);
  private tokenService = inject(TokenService);

  user = this.tokenService.getUser();

  logout(): void {
    this.authService.logout();
  }
}