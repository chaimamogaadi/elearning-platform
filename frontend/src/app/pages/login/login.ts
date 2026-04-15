import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { RouterLink, Router } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class LoginComponent {

  private fb          = inject(FormBuilder);
  private authService = inject(AuthService);
  private router      = inject(Router);

  isLoading    = false;
  errorMessage = '';
  hidePassword = true;

  form = this.fb.group({
    email:    ['', [Validators.required, Validators.email]],
    password: ['', Validators.required]
  });

onSubmit(): void {
  if (this.form.invalid) return;

  this.isLoading    = true;
  this.errorMessage = '';

  this.authService.login(this.form.value).subscribe({
    next: (res) => {
      this.isLoading = false;
      this.authService.handleLoginSuccess(res);

      if (res.role === 'ADMIN') {
        this.router.navigate(['/admin/overview']);
      } else if (res.role === 'INSTRUCTOR') {
        this.router.navigate(['/instructor/overview']);
      } else {
        this.router.navigate(['/student/dashboard']);
      }
    },
    error: (err) => {
      this.isLoading    = false;
      this.errorMessage = err.error?.message || 'Invalid email or password.';
    }
  });
}
}