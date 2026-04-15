import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { RouterLink, ActivatedRoute, Router } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [
    CommonModule, ReactiveFormsModule, RouterLink,
    MatFormFieldModule, MatInputModule,
    MatButtonModule, MatIconModule, MatProgressSpinnerModule
  ],
  templateUrl: './reset-password.html',
  styleUrl: './reset-password.css'
})
export class ResetPasswordComponent implements OnInit {

  private fb          = inject(FormBuilder);
  private authService = inject(AuthService);
  private route       = inject(ActivatedRoute);
  private router      = inject(Router);

  isLoading      = false;
  errorMessage   = '';
  successMessage = '';
  hidePassword   = true;
  token          = '';

  form = this.fb.group({
    newPassword:     ['', [Validators.required, Validators.minLength(6)]],
    confirmPassword: ['', [Validators.required]]
  });

  ngOnInit(): void {
    // ✅ Use queryParamMap for reliable token reading
    this.route.queryParamMap.subscribe((params) => {
    const token = params.get('token');

    if (!token) {
      this.errorMessage = 'Invalid or missing token.';
      return;
    }

    this.token = token;
  });
  }

  get passwordMismatch(): boolean {
    const p = this.form.value;
    return p.newPassword !== p.confirmPassword && !!p.confirmPassword;
  }

  onSubmit(): void {
    if (this.form.invalid || !this.token) return;

    if (this.passwordMismatch) {
      this.errorMessage = 'Passwords do not match.';
      return;
    }

    this.isLoading    = true;
    this.errorMessage = '';

    this.authService.resetPassword(
      this.token,
      this.form.value.newPassword!
    ).subscribe({
      next: (res: { message: string; }) => {
        this.isLoading      = false;
        // ✅ Read message from JSON response
        this.successMessage = res.message || 'Password reset successfully!';
        setTimeout(() => this.router.navigate(['/login']), 2500);
      },
      error: (err: { error: { message: string; }; }) => {
        this.isLoading    = false;
        this.errorMessage = err.error?.message || 'Reset failed. Please try again.';
      }
    });
  }
}