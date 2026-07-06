export interface ApiErrorResponse {
  timestamp?: string;
  status?: number;
  error?: string;
  message?: string;
  path?: string;
  fieldErrors?: Record<string, string>;
}

export interface AppError {
  message: string;
  fieldErrors: Record<string, string>;
  status: number;
}
