import { HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ApiErrorResponse, AppError } from './app-error';

@Injectable({
  providedIn: 'root'
})
export class ErrorService {

  toAppError(error: unknown): AppError {
    if (!(error instanceof HttpErrorResponse)) {
      return {
        message: 'Something went wrong. Please try again.',
        fieldErrors: {},
        status: 0
      };
    }

    const apiError = this.extractApiError(error);

    return {
      message: this.mapMessage(error.status, apiError?.message),
      fieldErrors: apiError?.fieldErrors || {},
      status: error.status
    };
  }

  private extractApiError(error: HttpErrorResponse): ApiErrorResponse | null {
    return typeof error.error === 'object' && error.error !== null
      ? error.error as ApiErrorResponse
      : null;
  }

  private mapMessage(status: number, backendMessage?: string): string {
    if (status === 0) {
      return 'Unable to connect to server. Please try again later.';
    }

    if (backendMessage === 'Gemini is temporarily busy. Please try again in a minute.') {
      return 'AI service is busy right now. Please try again shortly.';
    }

    if (backendMessage) {
      return backendMessage;
    }

    const messages: Record<number, string> = {
      400: 'Please check the highlighted fields.',
      401: 'Please login again to continue.',
      403: 'You do not have permission to access this item.',
      404: 'The requested item could not be found.',
      409: 'This request conflicts with existing data.',
      500: 'Something went wrong on the server. Please try again.'
    };

    return messages[status] || 'Something went wrong. Please try again.';
  }
}
