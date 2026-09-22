import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { IconComponent } from '../../components/icon/icon.component';
import { Role } from '../../models/user.model';
import { ROLE_COLORS } from '../../models/role-style';

interface NavItem {
  path: string;
  icon: string;
  label: string;
  adminOnly?: boolean;
}

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive, IconComponent],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css'],
})
export class HeaderComponent {
  roleColors = ROLE_COLORS;

  nav: NavItem[] = [
    { path: '/dashboard',    icon: 'dashboard', label: 'Tableau de bord' },
    { path: '/produtos',     icon: 'box',       label: 'Produits & Stock' },
    { path: '/categories',   icon: 'tag',       label: 'Catégories' },
    { path: '/fornecedores', icon: 'truck',     label: 'Fournisseurs' },
    { path: '/mouvements',   icon: 'swap',      label: 'Mouvements' },
    { path: '/customers',    icon: 'users',     label: 'Clients' },
    { path: '/orders',       icon: 'cart',      label: 'Commandes' },
    { path: '/payments',     icon: 'key',       label: 'Paiements' },
    { path: '/usuarios',     icon: 'shield',    label: 'Utilisateurs', adminOnly: true },
    { path: '/settings',     icon: 'settings',  label: 'Paramètres' },
  ];

  constructor(public auth: AuthService) {}

  get visibleNav(): NavItem[] {
    return this.nav.filter(i => !i.adminOnly || this.auth.isAdmin());
  }

  getRoleColor(role: Role): string {
    return this.roleColors[role];
  }
}
