import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';

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
  errorMessage = '';
  isSubmitting = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  onNameInput(event: Event): void {
    this.name = (event.target as HTMLInputElement).value;
  }

  onEmailInput(event: Event): void {
    this.email = (event.target as HTMLInputElement).value;
  }

  onPasswordInput(event: Event): void {
    this.password = (event.target as HTMLInputElement).value;
  }

  register(): void {
    this.errorMessage = '';

    if (!this.name.trim() || !this.email.trim() || !this.password.trim()) {
      this.errorMessage = 'Please complete all fields.';
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
        console.error(error);
        this.errorMessage = error?.error?.message || 'Registration failed. Please try again.';
        this.isSubmitting = false;
      }
    });
  }
}
