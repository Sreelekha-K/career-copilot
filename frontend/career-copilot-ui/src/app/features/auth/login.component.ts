import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-login',
  imports: [RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './auth.css'
})
export class LoginComponent {

  email = '';
  password = '';
  errorMessage = '';
  isSubmitting = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  onEmailInput(event: Event): void {
    this.email = (event.target as HTMLInputElement).value;
  }

  onPasswordInput(event: Event): void {
    this.password = (event.target as HTMLInputElement).value;
  }

  login(): void {
    this.errorMessage = '';

    if (!this.email.trim() || !this.password.trim()) {
      this.errorMessage = 'Please enter your email and password.';
      return;
    }

    this.isSubmitting = true;

    this.authService.login({
      email: this.email.trim(),
      password: this.password
    }).subscribe({
      next: () => {
        this.isSubmitting = false;
        this.router.navigate(['/dashboard']);
      },
      error: (error) => {
        console.error(error);
        this.errorMessage = error?.error?.message || 'Login failed. Please check your credentials.';
        this.isSubmitting = false;
      }
    });
  }
}
