import { Routes } from '@angular/router';
import { authGuard } from './core/auth/auth.guard';
import { DashboardComponent } from './features/dashboard/dashboard';
import { LoginComponent } from './features/auth/login.component';
import { RegisterComponent } from './features/auth/register.component';
import { ForgotPasswordComponent } from './features/auth/forgot-password.component';
import { LandingComponent } from './features/landing/landing.component';

export const routes: Routes = [
  { path: '', component: LandingComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'forgot-password', component: ForgotPasswordComponent },
  { path: 'dashboard', component: DashboardComponent, canActivate: [authGuard] },
  { path: 'resume-manager', component: DashboardComponent, canActivate: [authGuard] },
  { path: 'job-match', component: DashboardComponent, canActivate: [authGuard] },
  { path: 'resume-analysis', component: DashboardComponent, canActivate: [authGuard] },
  { path: 'skill-roadmap', component: DashboardComponent, canActivate: [authGuard] },
  { path: 'interview-prep', component: DashboardComponent, canActivate: [authGuard] },
  { path: '**', redirectTo: '' }
];
