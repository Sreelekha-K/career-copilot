import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-forgot-password',
  imports: [RouterLink],
  templateUrl: './forgot-password.component.html',
  styleUrl: './auth.css'
})
export class ForgotPasswordComponent {

  email = '';

  onEmailInput(event: Event): void {
    this.email = (event.target as HTMLInputElement).value;
  }
}
