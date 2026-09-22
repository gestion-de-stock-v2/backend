import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { IconComponent } from '../../components/icon/icon.component';

@Component({
  selector: 'app-not-found',
  standalone: true,
  imports: [CommonModule, RouterLink, IconComponent],
  template: `
    <div class="nf-wrap">
      <div class="nf-card">
        <div class="nf-icon">
          <app-icon name="alert" [size]="40" />
        </div>
        <p class="nf-code">404</p>
        <h1>Page introuvable</h1>
        <p class="nf-text">
          La page que vous recherchez n'existe pas ou a été déplacée.
        </p>
        <div class="nf-actions">
          <a routerLink="/dashboard" class="btn-primary">
            <app-icon name="dashboard" [size]="18" />
            <span>Tableau de bord</span>
          </a>
          <a routerLink="/" class="btn-secondary">
            <app-icon name="info" [size]="18" />
            <span>Accueil</span>
          </a>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .nf-wrap {
      min-height: 100vh;
      display: flex;
      align-items: center;
      justify-content: center;
      padding: var(--space-xl) var(--gutter);
      background: var(--surface);
    }

    .nf-card {
      width: 100%;
      max-width: 480px;
      padding: clamp(1.75rem, 5vw, 3rem);
      text-align: center;
      background: var(--surface-raised);
      border: 1px solid var(--border);
      border-radius: var(--r-xl);
      box-shadow: var(--elev-2);
    }

    .nf-icon {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 80px;
      height: 80px;
      margin: 0 auto var(--space-md);
      border-radius: var(--r-lg);
      background: var(--accent-tint);
      color: var(--accent);
    }

    .nf-code {
      margin: 0;
      font-size: 56px;
      font-weight: 800;
      line-height: 1;
      letter-spacing: -0.03em;
      color: var(--primary);
    }

    .nf-card h1 { margin: var(--space-sm) 0; font-size: 22px; font-weight: 700; }
    .nf-text { margin: 0 0 var(--space-xl); font-size: 14px; color: var(--ink-muted); }

    .nf-actions {
      display: flex;
      gap: var(--space-sm);
      justify-content: center;
      flex-wrap: wrap;
    }
    .nf-actions a { text-decoration: none; }

    @media (max-width: 480px) {
      .nf-actions { flex-direction: column; }
      .nf-actions a { width: 100%; }
    }
  `],
})
export class NotFoundComponent {}
