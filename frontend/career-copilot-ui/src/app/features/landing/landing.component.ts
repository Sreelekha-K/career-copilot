import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AnonymousAnalysisPreview, Api } from '../../core/services/api';
import { ErrorService } from '../../core/errors/error.service';
import { ToastService } from '../../core/errors/toast.service';

@Component({
  selector: 'app-landing',
  imports: [RouterLink],
  templateUrl: './landing.component.html',
  styleUrl: './landing.component.css'
})
export class LandingComponent {

  demoState: 'idle' | 'loading' | 'ready' | 'error' = 'idle';
  contactSent = false;
  selectedDemoFile: File | null = null;
  jobDescription = '';
  demoError = '';
  preview: AnonymousAnalysisPreview | null = null;
  showPremiumModal = false;
  loadingStepIndex = 0;

  readonly loadingSteps = [
    'Analyzing Resume...',
    'Extracting Skills...',
    'Comparing Job Description...',
    'Optimizing Resume...',
    'Preparing Preview...'
  ];

  readonly stats = [
    { value: '1000+', label: 'Resume Analyses' },
    { value: '500+', label: 'Active Users' },
    { value: '92%', label: 'ATS Accuracy' },
    { value: '2 min', label: 'Average Analysis Time' }
  ];

  readonly steps = [
    { number: '01', title: 'Upload Resume', text: 'Upload your existing resume in PDF or DOCX format.', icon: 'UP' },
    { number: '02', title: 'Paste Job Description', text: 'Copy any target job description from LinkedIn, Naukri, or a company career page.', icon: 'JD' },
    { number: '03', title: 'AI Analysis', text: 'Career Copilot compares both documents for skills, ATS keywords, and gaps.', icon: 'AI' },
    { number: '04', title: 'Download Better Resume', text: 'Receive a tailored, ATS-aware resume version ready to apply.', icon: 'OK' }
  ];

  readonly features = [
    { title: 'Increase ATS Score', text: 'Improve ATS compatibility before your resume reaches a recruiter.' },
    { title: 'Tailor Every Resume', text: 'Stop sending the same resume everywhere. Customize for each role.' },
    { title: 'AI Suggestions', text: 'Get keyword, project, skill, and resume refinement suggestions.' },
    { title: 'Save Time', text: 'No prompt engineering. Upload, paste the JD, analyze, and move forward.' }
  ];

  readonly testimonials = [
    { quote: 'I increased my ATS score from 48% to 87%.', name: 'Java Developer', initials: 'JD' },
    { quote: 'My resume finally started getting interview calls.', name: 'Frontend Engineer', initials: 'FE' },
    { quote: 'The platform saves hours before every application.', name: 'Full Stack Engineer', initials: 'FS' }
  ];

  readonly faqs = [
    { question: 'Will my resume be stored?', answer: 'Anonymous uploads are not saved. Registered users can save resume history inside their account.' },
    { question: 'Which file formats are supported?', answer: 'Career Copilot supports PDF and DOCX resumes for analysis.' },
    { question: 'Can I optimize resumes for different companies?', answer: 'Yes. Each job description generates a unique resume analysis and optimization plan.' }
  ];

  readonly techStack = [
    'Java 21',
    'Spring Boot',
    'Angular',
    'PostgreSQL',
    'JWT Authentication',
    'Spring Security',
    'REST APIs',
    'Gemini AI',
    'Resume Parsing',
    'ATS Analysis'
  ];

  constructor(
    private apiService: Api,
    private errorService: ErrorService,
    private toastService: ToastService
  ) {}

  onDemoFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0] || null;

    if (!file) {
      return;
    }

    if (!this.isSupportedFile(file)) {
      this.demoError = 'Unsupported file type. Please upload a PDF or DOCX resume.';
      this.selectedDemoFile = null;
      input.value = '';
      return;
    }

    this.demoError = '';
    this.selectedDemoFile = file;
  }

  onJobDescriptionInput(event: Event): void {
    this.jobDescription = (event.target as HTMLTextAreaElement).value;
    this.demoError = '';
  }

  runDemo(): void {
    if (!this.selectedDemoFile) {
      this.demoError = 'Please upload a resume before analyzing.';
      return;
    }

    if (!this.jobDescription.trim()) {
      this.demoError = 'Please paste a job description before analyzing.';
      return;
    }

    this.demoState = 'loading';
    this.demoError = '';
    this.preview = null;
    this.loadingStepIndex = 0;
    this.advanceLoadingSteps();

    this.apiService.analyzeAnonymousPreview(this.selectedDemoFile, this.jobDescription).subscribe({
      next: (response) => {
        this.preview = response;
        this.demoState = 'ready';
        this.loadingStepIndex = this.loadingSteps.length - 1;
      },
      error: (error) => {
        this.demoState = 'error';
        this.demoError = this.errorService.toAppError(error).message || 'AI preview failed. Please try again.';
      }
    });
  }

  copyPreview(): void {
    if (!this.preview) {
      return;
    }

    const content = this.buildPreviewText(this.preview);

    navigator.clipboard.writeText(content).then(() => {
      this.toastService.success('Optimized preview copied.');
    }).catch(() => {
      this.toastService.error('Could not copy preview. Please select and copy manually.');
    });
  }

  clearDemo(): void {
    this.selectedDemoFile = null;
    this.jobDescription = '';
    this.demoError = '';
    this.preview = null;
    this.demoState = 'idle';
    this.loadingStepIndex = 0;
  }

  openPremiumModal(): void {
    this.showPremiumModal = true;
  }

  closePremiumModal(): void {
    this.showPremiumModal = false;
  }

  submitContact(event: Event): void {
    event.preventDefault();
    this.contactSent = true;
  }

  private isSupportedFile(file: File): boolean {
    const fileName = file.name.toLowerCase();
    return fileName.endsWith('.pdf') || fileName.endsWith('.docx');
  }

  private advanceLoadingSteps(): void {
    this.loadingSteps.forEach((_, index) => {
      window.setTimeout(() => {
        if (this.demoState === 'loading') {
          this.loadingStepIndex = index;
        }
      }, index * 850);
    });
  }

  private buildPreviewText(preview: AnonymousAnalysisPreview): string {
    return [
      'Professional Summary',
      preview.professionalSummary,
      '',
      'Improved Skills',
      ...(preview.improvedSkills || []).map((skill) => `- ${skill}`),
      '',
      'Improved Experience',
      ...(preview.improvedExperienceBullets || []).map((bullet) => `- ${bullet}`),
      '',
      'Recommended ATS Keywords',
      ...(preview.atsKeywords || []).map((keyword) => `- ${keyword}`),
      '',
      'Quick Suggestions',
      ...(preview.quickSuggestions || []).map((suggestion) => `- ${suggestion}`)
    ].join('\n');
  }
}
