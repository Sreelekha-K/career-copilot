import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';
import { AnalysisStateService } from '../../core/services/analysis-state.service';
import { Api, JobAnalysisResponse, Resume } from '../../core/services/api';
import { ErrorService } from '../../core/errors/error.service';

type DashboardSection =
  | 'dashboard'
  | 'resume-manager'
  | 'job-match'
  | 'resume-analysis'
  | 'skill-roadmap'
  | 'interview-prep';

@Component({
  selector: 'app-dashboard',
  imports: [RouterLink],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class DashboardComponent implements OnInit {

  backendStatus = 'Checking backend connection...';
  selectedFile: File | null = null;
  uploadMessage = '';
  uploadError = '';
  resumes: Resume[] = [];
  isLoadingResumes = false;
  resumeError = '';
  deletingResumeId: number | null = null;
  selectedResumeId: number | null = null;
  jobDescription = '';
  analysisResult: JobAnalysisResponse | null = null;
  isAnalyzing = false;
  analysisError = '';
  activeSection: DashboardSection = 'dashboard';

  readonly sections: { id: DashboardSection; label: string; icon: string }[] = [
    { id: 'dashboard', label: 'Dashboard', icon: 'D' },
    { id: 'resume-manager', label: 'Resume Manager', icon: 'R' },
    { id: 'job-match', label: 'Job Match', icon: 'M' },
    { id: 'resume-analysis', label: 'Resume Analysis', icon: 'A' },
    { id: 'skill-roadmap', label: 'Skill Roadmap', icon: 'S' },
    { id: 'interview-prep', label: 'Interview Prep', icon: 'I' }
  ];

  constructor(
    private apiService: Api,
    private authService: AuthService,
    private analysisStateService: AnalysisStateService,
    private errorService: ErrorService,
    private route: ActivatedRoute,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  get latestAtsScore(): string {
    return this.analysisResult ? `${this.analysisResult.atsScore}%` : 'Pending';
  }

  get userName(): string {
    return this.authService.getCurrentUser()?.name?.trim() || '';
  }

  get welcomeMessage(): string {
    return this.userName ? `Hello, ${this.userName}` : 'Hello';
  }

  get latestMatchScore(): string {
    return this.analysisResult ? `${this.analysisResult.jobMatchScore}%` : 'Pending';
  }

  get missingSkillsCount(): number {
    return this.analysisResult?.missingSkills?.length ?? 0;
  }

  get interviewReadiness(): string {
    if (!this.analysisResult) {
      return 'Pending';
    }

    const questionCount = this.analysisResult.technicalQuestions.length
      + this.analysisResult.behavioralQuestions.length;

    return `${questionCount} questions`;
  }

  get nextRecommendedAction(): string {
    if (this.resumes.length === 0) {
      return 'Upload your first resume to start career analysis.';
    }

    if (!this.analysisResult) {
      return 'Paste a target job description and run Job Match.';
    }

    if (this.missingSkillsCount > 0) {
      return `Start with ${this.analysisResult.missingSkills[0]} in your skill roadmap.`;
    }

    return 'Review interview questions and polish your tailored resume.';
  }

  ngOnInit(): void {
    this.syncSectionFromRoute();
    this.analysisResult = this.analysisStateService.getLatestAnalysis();

    this.apiService.getBackendStatus().subscribe({
      next: (response) => {
        this.backendStatus = response;
        this.cdr.detectChanges();
      },
      error: () => {
        this.backendStatus = 'Backend connection failed!';
        this.cdr.detectChanges();
      }
    });

    this.loadResumes();
  }

  setActiveSection(section: DashboardSection): void {
    this.activeSection = section;
    this.router.navigate(['/', section]);
  }

  logout(): void {
    this.authService.logout();
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;

    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
    }
  }

  uploadResume(): void {
    if (!this.selectedFile) {
      this.uploadMessage = '';
      this.uploadError = 'Please select a file first.';
      return;
    }

    this.uploadError = '';

    this.apiService.uploadResume(this.selectedFile).subscribe({
      next: (response) => {
        this.uploadMessage = 'Resume uploaded successfully: ' + response.fileName;
        this.uploadError = '';
        this.selectedFile = null;
        this.loadResumes();
        this.cdr.detectChanges();
      },
      error: (error) => {
        this.uploadMessage = '';
        this.uploadError = this.getFriendlyErrorMessage(error, 'Resume upload failed. Please try again.');
        this.cdr.detectChanges();
      }
    });
  }

  loadResumes(): void {
    this.isLoadingResumes = true;
    this.resumeError = '';

    this.apiService.getAllResumes().subscribe({
      next: (resumes) => {
        this.resumes = resumes;
        this.isLoadingResumes = false;

        if (!this.selectedResumeId && resumes.length > 0) {
          this.selectedResumeId = resumes[0].id;
        }

        this.cdr.detectChanges();
      },
      error: (error) => {
        this.resumeError = this.getFriendlyErrorMessage(error, 'Could not load resumes');
        this.isLoadingResumes = false;
        this.cdr.detectChanges();
      }
    });
  }

  deleteResume(resume: Resume): void {
    const confirmed = window.confirm(`Delete "${resume.fileName}"?`);

    if (!confirmed) {
      return;
    }

    this.deletingResumeId = resume.id;

    this.apiService.deleteResume(resume.id).subscribe({
      next: () => {
        this.uploadMessage = `${resume.fileName} deleted`;

        if (this.selectedResumeId === resume.id) {
          this.selectedResumeId = null;
          this.analysisResult = null;
          this.analysisStateService.clearAnalysis();
        }

        this.deletingResumeId = null;
        this.loadResumes();
        this.cdr.detectChanges();
      },
      error: (error) => {
        this.resumeError = this.getFriendlyErrorMessage(error, 'Could not delete resume');
        this.deletingResumeId = null;
        this.cdr.detectChanges();
      }
    });
  }

  onResumeSelected(event: Event): void {
    const select = event.target as HTMLSelectElement;
    this.selectedResumeId = select.value ? Number(select.value) : null;
  }

  onJobDescriptionInput(event: Event): void {
    const textarea = event.target as HTMLTextAreaElement;
    this.jobDescription = textarea.value;
  }

  analyzeJobMatch(): void {
    if (!this.selectedResumeId) {
      this.analysisError = 'Please select a resume first';
      return;
    }

    if (!this.jobDescription.trim()) {
      this.analysisError = 'Please paste a job description';
      return;
    }

    this.isAnalyzing = true;
    this.analysisError = '';

    this.apiService.analyzeJobMatch({
      resumeId: this.selectedResumeId,
      jobDescription: this.jobDescription
    }).subscribe({
      next: (response) => {
        this.analysisResult = response;
        this.analysisStateService.saveAnalysis(response);
        this.isAnalyzing = false;
        this.setActiveSection('job-match');
        this.cdr.detectChanges();
      },
      error: (error) => {
        this.analysisError = this.getFriendlyErrorMessage(error, 'Could not analyze resume match');
        this.isAnalyzing = false;
        this.cdr.detectChanges();
      }
    });
  }

  formatFileSize(bytes: number | null | undefined): string {
    if (!bytes) {
      return '0 B';
    }

    const units = ['B', 'KB', 'MB', 'GB'];
    let size = bytes;
    let unitIndex = 0;

    while (size >= 1024 && unitIndex < units.length - 1) {
      size = size / 1024;
      unitIndex++;
    }

    return `${size.toFixed(unitIndex === 0 ? 0 : 1)} ${units[unitIndex]}`;
  }

  formatUploadDate(uploadedAt: string): string {
    if (!uploadedAt) {
      return 'Not available';
    }

    return new Intl.DateTimeFormat('en', {
      dateStyle: 'medium',
      timeStyle: 'short'
    }).format(new Date(uploadedAt));
  }

  private syncSectionFromRoute(): void {
    const path = this.route.snapshot.routeConfig?.path as DashboardSection | undefined;
    const sectionExists = this.sections.some((section) => section.id === path);

    this.activeSection = sectionExists && path ? path : 'dashboard';
  }

  private getFriendlyErrorMessage(error: any, fallbackMessage: string): string {
    return this.errorService.toAppError(error).message || fallbackMessage;
  }
}
