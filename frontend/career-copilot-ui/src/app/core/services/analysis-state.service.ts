import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { JobAnalysisResponse } from './api';

@Injectable({
  providedIn: 'root'
})
export class AnalysisStateService {

  private readonly storageKey = 'career_copilot_latest_analysis';
  private readonly latestAnalysisSubject = new BehaviorSubject<JobAnalysisResponse | null>(
    this.restoreFromSessionStorage()
  );

  readonly latestAnalysis$ = this.latestAnalysisSubject.asObservable();

  getLatestAnalysis(): JobAnalysisResponse | null {
    return this.latestAnalysisSubject.value;
  }

  saveAnalysis(analysis: JobAnalysisResponse): void {
    this.latestAnalysisSubject.next(analysis);
    this.saveToSessionStorage(analysis);
  }

  clearAnalysis(): void {
    this.latestAnalysisSubject.next(null);
    this.removeFromSessionStorage();
  }

  private restoreFromSessionStorage(): JobAnalysisResponse | null {
    try {
      const storedAnalysis = sessionStorage.getItem(this.storageKey);
      return storedAnalysis ? JSON.parse(storedAnalysis) as JobAnalysisResponse : null;
    } catch {
      this.removeFromSessionStorage();
      return null;
    }
  }

  private saveToSessionStorage(analysis: JobAnalysisResponse): void {
    try {
      sessionStorage.setItem(this.storageKey, JSON.stringify(analysis));
    } catch {
      // In-memory state still works if browser storage is unavailable.
    }
  }

  private removeFromSessionStorage(): void {
    try {
      sessionStorage.removeItem(this.storageKey);
    } catch {
      // Nothing else to clean up if browser storage is unavailable.
    }
  }
}
