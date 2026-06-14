import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Api } from './core/services/api';

@Component({
  selector: 'app-root',
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  backendStatus = 'Checking backend connection...';

  constructor(
    private apiService: Api,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    console.log('ngOnInit called');

    this.apiService.getBackendStatus().subscribe({
      next: (response) => {
        console.log('SUCCESS', response);
        this.backendStatus = response;
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.log('ERROR', error);
        this.backendStatus = 'Backend connection failed!';
        this.cdr.detectChanges();
      }
    });
  }
}