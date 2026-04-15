import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { AuthService } from '../../services/auth';
import { TokenService } from '../../services/token';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, MatButtonModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class DashboardComponent implements OnInit {

  private authService  = inject(AuthService);
  private tokenService = inject(TokenService);

  user: any = null;

  ngOnInit(): void {
    this.user = this.tokenService.getUser();
  }

  logout(): void {
    this.authService.logout();
  }
}