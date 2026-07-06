import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';
import { ErrorService } from '../../core/errors/error.service';

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
  showPassword = false;
  fieldErrors = {
    email: '',
    password: ''
  };

  constructor(
    private authService: AuthService,
    private router: Router,
    private errorService: ErrorService
  ) {}

  onEmailInput(event: Event): void {
    this.email = (event.target as HTMLInputElement).value;
    this.fieldErrors.email = '';
  }

  onPasswordInput(event: Event): void {
    this.password = (event.target as HTMLInputElement).value;
    this.fieldErrors.password = '';
  }

  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }

  login(): void {
    this.errorMessage = '';

    if (!this.validateForm()) {
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
        this.applyBackendErrors(error, 'Unable to login right now.');
        this.isSubmitting = false;
      }
    });
  }

  private applyBackendErrors(error: unknown, fallbackMessage: string): void {
    const appError = this.errorService.toAppError(error);
    const fieldErrors = appError.fieldErrors;

    if (Object.keys(fieldErrors).length > 0) {
      this.fieldErrors = {
        email: fieldErrors['email'] || '',
        password: fieldErrors['password'] || ''
      };
    }

    this.errorMessage = appError.status === 0
      ? fallbackMessage
      : appError.message || fallbackMessage;
  }

  private validateForm(): boolean {
    this.fieldErrors = {
      email: this.validateEmail(this.email),
      password: this.validatePassword(this.password)
    };

    return !this.fieldErrors.email && !this.fieldErrors.password;
  }

  private validateEmail(email: string): string {
    const trimmedEmail = email.trim();

    if (!trimmedEmail) {
      return 'Email is required.';
    }

    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(trimmedEmail)
      ? ''
      : 'Please enter a valid email address.';
  }

  private validatePassword(password: string): string {
    return password ? '' : 'Password is required.';
  }
}
