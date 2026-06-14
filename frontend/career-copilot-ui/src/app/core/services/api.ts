import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class Api {

  private readonly http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8080';

  getBackendStatus(): Observable<string> {
    return this.http.get(`${this.baseUrl}/api/status`, {
      responseType: 'text'
    });
  }
}