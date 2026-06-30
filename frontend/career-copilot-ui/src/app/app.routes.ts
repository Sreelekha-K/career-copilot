import { Routes } from '@angular/router';
import { authGuard } from './core/auth/auth.guard';
import { DashboardComponent } from './features/dashboard/dashboard';
import { LoginComponent } from './features/auth/login.component';
import { RegisterComponent } from './features/auth/register.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'dashboard', component: DashboardComponent, canActivate: [authGuard] },
  { path: 'resume-manager', component: DashboardComponent, canActivate: [authGuard] },
  { path: 'job-match', component: DashboardComponent, canActivate: [authGuard] },
  { path: 'resume-analysis', component: DashboardComponent, canActivate: [authGuard] },
  { path: 'skill-roadmap', component: DashboardComponent, canActivate: [authGuard] },
  { path: 'interview-prep', component: DashboardComponent, canActivate: [authGuard] },
  { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
  { path: '**', redirectTo: 'dashboard' }
];
