import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [
    CommonModule, ReactiveFormsModule, RouterLink,
    MatFormFieldModule, MatInputModule,
    MatButtonModule, MatIconModule, MatProgressSpinnerModule
  ],
  templateUrl: './forgot-password.html',
  styleUrl: './forgot-password.css'
})
export class ForgotPasswordComponent {

  private fb          = inject(FormBuilder);
  private authService = inject(AuthService);

  isLoading      = false;
  errorMessage   = '';
  successMessage = '';

  form = this.fb.group({
    email: ['', [Validators.required, Validators.email]]
  });

  onSubmit(): void {
    if (this.form.invalid) return;

    this.isLoading      = true;
    this.errorMessage   = '';
    this.successMessage = '';

    this.authService.forgotPassword(this.form.value.email!).subscribe({
      next: (res: { message: string; }) => {
        this.isLoading      = false;
        // ✅ Read the message field from JSON response
        this.successMessage = res.message || 'Reset link sent! Check your email.';
        this.form.reset();
      },
      error: (err: { error: { message: string; }; }) => {
        this.isLoading    = false;
        // ✅ Read message from error response body
        this.errorMessage = err.error?.message || 'No account found with this email.';
      }
    });
  }
}