import { Component } from '@angular/core';
import { AsyncPipe } from '@angular/common';
import { ToastService } from './toast.service';

@Component({
  selector: 'app-toast-container',
  imports: [AsyncPipe],
  template: `
    <section class="toast-stack" aria-live="polite" aria-label="Notifications">
      @for (toast of toastService.toasts$ | async; track toast.id) {
        <button
          class="toast"
          type="button"
          [class.error]="toast.type === 'error'"
          [class.info]="toast.type === 'info'"
          [class.success]="toast.type === 'success'"
          (click)="toastService.dismiss(toast.id)"
        >
          {{ toast.message }}
        </button>
      }
    </section>
  `,
  styles: [`
    .toast-stack {
      position: fixed;
      right: 18px;
      bottom: 18px;
      z-index: 1000;
      display: grid;
      gap: 10px;
      width: min(360px, calc(100vw - 36px));
    }

    .toast {
      border: 1px solid rgba(255, 255, 255, 0.76);
      border-radius: 18px;
      padding: 13px 15px;
      color: #4b4668;
      font: 850 14px/1.45 Inter, ui-sans-serif, system-ui, "Segoe UI", Arial, sans-serif;
      text-align: left;
      background: #fff0f5;
      box-shadow: 0 18px 42px rgba(104, 96, 146, 0.18);
      cursor: pointer;
    }

    .toast.info {
      background: #eef7ff;
    }

    .toast.success {
      background: #e9faef;
    }
  `]
})
export class ToastContainerComponent {

  constructor(public toastService: ToastService) {}
}
