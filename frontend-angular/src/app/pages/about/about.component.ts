import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { IconComponent } from '../../components/icon/icon.component';

interface Feature {
  icon: string;
  title: string;
  text: string;
}

@Component({
  selector: 'app-about',
  standalone: true,
  imports: [CommonModule, RouterLink, IconComponent],
  template: `
    <div class="about-hero">
      <div class="about-logo"><app-icon name="box" [size]="36" /></div>
      <h1>Gestion de Stock</h1>
      <p class="about-lead">
        Plateforme d'inventaire et de commandes : produits, catégories,
        fournisseurs, mouvements, clients, commandes et paiements.
      </p>
    </div>

    <div class="about-grid">
      <article class="feature" *ngFor="let f of features">
        <app-icon [name]="f.icon" [size]="22" />
        <h3>{{ f.title }}</h3>
        <p>{{ f.text }}</p>
      </article>
    </div>

    <div class="about-cta">
      <a routerLink="/dashboard" class="btn-primary">
        <app-icon name="dashboard" [size]="18" />
        <span>Aller au tableau de bord</span>
      </a>
    </div>
  `,
  styles: [`
    .about-hero { text-align: center; padding: var(--space-md) 0 var(--space-xl); }

    .about-logo {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 80px;
      height: 80px;
      margin: 0 auto var(--space-md);
      border-radius: var(--r-lg);
      background: var(--primary);
      color: #fff;
      box-shadow: var(--elev-2);
    }

    .about-hero h1 { margin: 0 0 var(--space-sm); }
    .about-lead {
      max-width: 60ch;
      margin: 0 auto;
      font-size: 16px;
      line-height: 24px;
      color: var(--ink-muted);
    }

    .about-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
      gap: var(--space-md);
      margin-bottom: var(--space-xl);
    }

    .feature {
      padding: var(--space-lg);
      background: var(--surface-raised);
      border: 1px solid var(--border);
      border-radius: var(--r-lg);
      box-shadow: var(--elev-1);
      transition: border-color .2s ease, box-shadow .2s ease, transform .2s ease;
    }
    .feature:hover {
      border-color: var(--primary-soft);
      box-shadow: var(--elev-2);
      transform: translateY(-3px);
    }

    .feature app-icon { color: var(--primary); }
    .feature h3 { margin: var(--space-sm) 0 6px; font-size: 16px; }
    .feature p { margin: 0; font-size: 14px; line-height: 21px; color: var(--ink-muted); }

    .about-cta { display: flex; justify-content: center; padding-bottom: var(--space-xl); }
    .about-cta a { text-decoration: none; }

    @media (prefers-reduced-motion: reduce) {
      .feature:hover { transform: none; }
    }
  `],
})
export class AboutComponent {
  /** Toutes les icônes référencées existent dans IconComponent. */
  features: Feature[] = [
    { icon: 'box',      title: 'Produits',        text: 'Catalogue avec prix, quantité disponible, catégorie et fournisseur associés.' },
    { icon: 'tag',      title: 'Catégories',      text: 'Organisation du catalogue pour un suivi lisible par famille de produits.' },
    { icon: 'truck',    title: 'Fournisseurs',    text: 'Coordonnées centralisées et rattachement aux produits approvisionnés.' },
    { icon: 'swap',     title: 'Mouvements',      text: "Entrées et sorties historisées, avec contrôle du stock disponible." },
    { icon: 'users',    title: 'Clients',         text: 'Référentiel clients utilisé lors de la création des commandes.' },
    { icon: 'cart',     title: 'Commandes',       text: 'Décrément de stock protégé contre la survente, compensé en cas d\'échec.' },
    { icon: 'shield',   title: 'Rôles et droits', text: 'Sept rôles métier, vérifiés côté serveur et non seulement masqués à l\'écran.' },
    { icon: 'mail',     title: 'Notifications',   text: 'Confirmations de commande et de paiement envoyées automatiquement par e-mail.' },
  ];
}
