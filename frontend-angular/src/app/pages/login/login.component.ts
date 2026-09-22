import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { IconComponent } from '../../components/icon/icon.component';
import { Role } from '../../models/user.model';

/** Profil de démonstration : la couleur provient des tokens de DESIGN.md. */
interface ProfilDemo {
  label: string;
  user: string;
  pass: string;
  icon: string;
  role: Role;
  color: string;
}

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, IconComponent, RouterLink],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  username = '';
  password = '';
  showPassword = false;
  error = '';
  loading = false;

  profils: ProfilDemo[] = [
    { label: 'Administrateur', user: 'admin',       pass: 'admin123!',       icon: 'crown',       role: 'ADMIN',       color: 'var(--danger)' },
    { label: 'Gérant',         user: 'gerant',      pass: 'gerant123!',      icon: 'briefcase',   role: 'GERANT',      color: 'var(--primary)' },
    { label: 'Magasinier',     user: 'magasinier',  pass: 'magasin123!',     icon: 'package',     role: 'MAGASINIER',  color: 'var(--accent)' },
    { label: 'Vendeur',        user: 'vendeur',     pass: 'vendeur123!',     icon: 'cart',        role: 'VENDEUR',     color: 'var(--success)' },
    { label: 'Acheteur',       user: 'acheteur',    pass: 'acheteur123!',    icon: 'shoppingBag', role: 'ACHETEUR',    color: 'var(--primary-bright)' },
    { label: 'Comptable',      user: 'comptable',   pass: 'comptable123!',   icon: 'chart',       role: 'COMPTABLE',   color: 'var(--accent-bright)' },
    { label: 'Observateur',    user: 'observateur', pass: 'observateur123!', icon: 'eye',         role: 'OBSERVATEUR', color: 'var(--ink-muted)' }
  ];

  constructor(private auth: AuthService, private router: Router) {}

  login(): void {
    this.error = '';
    this.loading = true;
    this.auth.login({ username: this.username, password: this.password }).subscribe({
      next: () => { this.loading = false; this.router.navigate(['/dashboard']); },
      error: () => { this.loading = false; this.error = 'Identifiants incorrects'; }
    });
  }

  remplir(user: string, pass: string): void {
    this.username = user;
    this.password = pass;
    this.error = '';
  }
}
