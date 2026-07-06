import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';
import { ErrorService } from '../../core/errors/error.service';

@Component({
  selector: 'app-register',
  imports: [RouterLink],
  templateUrl: './register.component.html',
  styleUrl: './auth.css'
})
export class RegisterComponent {

  name = '';
  email = '';
  password = '';
  confirmPassword = '';
  errorMessage = '';
  isSubmitting = false;
  showPassword = false;
  showConfirmPassword = false;
  fieldErrors = {
    name: '',
    email: '',
    password: '',
    confirmPassword: ''
  };

  constructor(
    private authService: AuthService,
    private router: Router,
    private errorService: ErrorService
  ) {}

  onNameInput(event: Event): void {
    this.name = (event.target as HTMLInputElement).value;
    this.fieldErrors.name = '';
  }

  onEmailInput(event: Event): void {
    this.email = (event.target as HTMLInputElement).value;
    this.fieldErrors.email = '';
  }

  onPasswordInput(event: Event): void {
    this.password = (event.target as HTMLInputElement).value;
    this.fieldErrors.password = '';
    this.fieldErrors.confirmPassword = '';
  }

  onConfirmPasswordInput(event: Event): void {
    this.confirmPassword = (event.target as HTMLInputElement).value;
    this.fieldErrors.confirmPassword = '';
  }

  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }

  toggleConfirmPasswordVisibility(): void {
    this.showConfirmPassword = !this.showConfirmPassword;
  }

  register(): void {
    this.errorMessage = '';

    if (!this.validateForm()) {
      return;
    }

    this.isSubmitting = true;

    this.authService.register({
      name: this.name.trim(),
      email: this.email.trim(),
      password: this.password
    }).subscribe({
      next: () => {
        this.isSubmitting = false;
        this.router.navigate(['/dashboard']);
      },
      error: (error) => {
        this.applyBackendErrors(error, 'Registration failed. Please try again.');
        this.isSubmitting = false;
      }
    });
  }

  private applyBackendErrors(error: unknown, fallbackMessage: string): void {
    const appError = this.errorService.toAppError(error);
    const fieldErrors = appError.fieldErrors;

    if (Object.keys(fieldErrors).length > 0) {
      this.fieldErrors = {
        name: fieldErrors['name'] || '',
        email: fieldErrors['email'] || '',
        password: fieldErrors['password'] || '',
        confirmPassword: ''
      };
    }

    this.errorMessage = appError.message || fallbackMessage;
  }

  private validateForm(): boolean {
    this.fieldErrors = {
      name: this.validateName(this.name),
      email: this.validateEmail(this.email),
      password: this.validatePassword(this.password),
      confirmPassword: this.validateConfirmPassword()
    };

    return !this.fieldErrors.name
      && !this.fieldErrors.email
      && !this.fieldErrors.password
      && !this.fieldErrors.confirmPassword;
  }

  private validateName(name: string): string {
    const trimmedName = name.trim();

    if (!trimmedName) {
      return 'Name is required.';
    }

    if (trimmedName.length < 3) {
      return 'Name must be at least 3 characters.';
    }

    return trimmedName.length <= 50 ? '' : 'Name must be 50 characters or fewer.';
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
    if (!password) {
      return 'Password is required.';
    }

    if (password.length < 10) {
      return 'Password must be at least 10 characters.';
    }

    if (!/[A-Z]/.test(password) || !/[a-z]/.test(password) || !/\d/.test(password) || !/[^A-Za-z0-9]/.test(password)) {
      return 'Password must contain: minimum 10 characters, one uppercase letter, one lowercase letter, one number, and one special character.';
    }

    return '';
  }

  private validateConfirmPassword(): string {
    if (!this.confirmPassword) {
      return 'Please confirm your password.';
    }

    return this.confirmPassword === this.password ? '' : 'Passwords do not match.';
  }
}
