import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ThemeService } from '../../services/theme.service';
import { IconComponent } from '../icon/icon.component';

@Component({
  selector: 'app-theme-toggle',
  standalone: true,
  imports: [CommonModule, IconComponent],
  template: `
    <button type="button" class="theme-btn" (click)="theme.toggle()"
            [attr.aria-label]="theme.theme() === 'dark' ? 'Passer en mode clair' : 'Passer en mode sombre'"
            [attr.aria-pressed]="theme.theme() === 'dark'">
      <app-icon [name]="theme.theme() === 'dark' ? 'sun' : 'moon'" [size]="18" />
    </button>
  `,
  styles: [`
    /* Cible tactile de 44px imposée par la charte, même si l'icône fait 18px. */
    .theme-btn {
      display: flex;
      align-items: center;
      justify-content: center;
      width: var(--touch-min);
      height: var(--touch-min);
      background: var(--surface-raised);
      border: 1px solid var(--border);
      border-radius: var(--r-md);
      color: var(--ink-soft);
      cursor: pointer;
      transition: background .18s ease, color .18s ease, border-color .18s ease;
    }
    .theme-btn:hover {
      background: var(--primary-tint);
      border-color: var(--primary);
      color: var(--primary);
    }
    .theme-btn:active { transform: scale(.96); }

    /* Variante posée sur un fond coloré (écrans publics). */
    :host(.on-dark) .theme-btn {
      background: rgba(255, 255, 255, .14);
      border-color: rgba(255, 255, 255, .28);
      color: #fff;
    }
    :host(.on-dark) .theme-btn:hover { background: rgba(255, 255, 255, .24); color: #fff; }
  `],
})
export class ThemeToggleComponent {
  constructor(public theme: ThemeService) {}
}
