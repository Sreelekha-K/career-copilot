import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface Resume {
  id: number;
  fileName: string;
  fileType: string;
  uploadedAt: string;
  fileSize: number;
  filePath: string;
  resumeTextPreview?: string;
  parsedTextPreview?: string;
}

export interface JobAnalysisRequest {
  resumeId: number;
  jobDescription: string;
}

export interface LearningRoadmapItem {
  skill: string;
  whyImportant: string;
  learningSteps: string[];
}

export interface JobAnalysisResponse {
  atsScore: number;
  jobMatchScore: number;
  matchedSkills: string[];
  missingSkills: string[];
  improvementSuggestions: string[];
  recommendedKeywords: string[];
  refinedResume: string;
  learningRoadmap: LearningRoadmapItem[];
  technicalQuestions: string[];
  behavioralQuestions: string[];
}

@Injectable({
  providedIn: 'root'
})
export class Api {

  private readonly http = inject(HttpClient);
  private readonly baseUrl = environment.apiUrl;

  getBackendStatus(): Observable<string> {
    return this.http.get(`${this.baseUrl}/api/status`, {
      responseType: 'text'
    });
  }

  uploadResume(file: File): Observable<Resume> {
    const formData = new FormData();
    formData.append('file', file);

    return this.http.post<Resume>(`${this.baseUrl}/api/resumes/upload`, formData);
  }

  getAllResumes(): Observable<Resume[]> {
    return this.http.get<Resume[]>(`${this.baseUrl}/api/resumes`);
  }

  deleteResume(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/api/resumes/${id}`);
  }

  analyzeJobMatch(request: JobAnalysisRequest): Observable<JobAnalysisResponse> {
    return this.http.post<JobAnalysisResponse>(`${this.baseUrl}/api/job-analysis/analyze`, request);
  }
}
