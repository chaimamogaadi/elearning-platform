import { Component, inject } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../services/auth';
import { TokenService } from '../../../services/token';

@Component({
  selector: 'app-instructor-layout',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, CommonModule],
  templateUrl: './instructor-layout.html',
  styleUrl: './instructor-layout.css'
})
export class InstructorLayoutComponent {
  private authService  = inject(AuthService);
  private tokenService = inject(TokenService);
  user = this.tokenService.getUser();
  logout() { this.authService.logout(); }
}