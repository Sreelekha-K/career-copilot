import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface AuthRequest {
  email: string;
  password: string;
}

export interface RegisterRequest extends AuthRequest {
  name: string;
}

export interface AuthResponse {
  token: string;
  tokenType?: string;
  userId?: number;
  name?: string;
  email?: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly baseUrl = `${environment.apiUrl}/api/auth`;
  private readonly tokenKey = 'career_copilot_jwt';
  private readonly userKey = 'career_copilot_user';

  register(request: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.baseUrl}/register`, request).pipe(
      tap((response) => this.saveSession(response))
    );
  }

  login(request: AuthRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.baseUrl}/login`, request).pipe(
      tap((response) => this.saveSession(response))
    );
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.userKey);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  isAuthenticated(): boolean {
    const token = this.getToken();
    return !!token && token.trim().length > 0;
  }

  getCurrentUser(): AuthResponse | null {
    const userJson = localStorage.getItem(this.userKey);

    if (!userJson) {
      return null;
    }

    try {
      return JSON.parse(userJson) as AuthResponse;
    } catch {
      localStorage.removeItem(this.userKey);
      return null;
    }
  }

  private saveSession(response: AuthResponse): void {
    localStorage.setItem(this.tokenKey, response.token);
    localStorage.setItem(this.userKey, JSON.stringify(response));
  }
}
