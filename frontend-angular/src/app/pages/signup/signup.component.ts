import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { IconComponent } from '../../components/icon/icon.component';

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, IconComponent],
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css'],
})
export class SignupComponent {
  username = '';
  password = '';
  confirmPassword = '';
  name = '';
  email = '';
  showPassword = false;
  loading = false;
  error = '';
  success = '';

  constructor(private auth: AuthService, private router: Router) {}

  get passwordStrength(): 'faible' | 'moyen' | 'fort' {
    const p = this.password;
    if (p.length < 8) return 'faible';
    let score = 0;
    if (p.length >= 12) score++;
    if (/[A-Z]/.test(p)) score++;
    if (/[0-9]/.test(p)) score++;
    if (/[^A-Za-z0-9]/.test(p)) score++;
    if (score <= 1) return 'faible';
    if (score <= 2) return 'moyen';
    return 'fort';
  }

  /**
   * Le role n'est volontairement pas transmis : le serveur attribue OBSERVATEUR
   * (lecture seule) a toute inscription libre. Laisser le client choisir son role
   * permettait de creer un compte administrateur sans aucune authentification.
   */
  signup(): void {
    this.error = '';
    this.success = '';

    if (!this.username.trim() || this.username.length < 3) {
      this.error = "Le nom d'utilisateur doit contenir au moins 3 caractères";
      return;
    }
    if (!this.name.trim()) {
      this.error = 'Le nom complet est obligatoire';
      return;
    }
    if (!this.email.trim() || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.email)) {
      this.error = 'Adresse email invalide';
      return;
    }
    if (this.password.length < 8) {
      this.error = 'Le mot de passe doit contenir au moins 8 caractères';
      return;
    }
    if (this.password !== this.confirmPassword) {
      this.error = 'Les mots de passe ne correspondent pas';
      return;
    }

    this.loading = true;
    this.auth.register({
      username: this.username,
      password: this.password,
      name: this.name,
      email: this.email,
    }).subscribe({
      next: () => {
        this.loading = false;
        this.success = 'Compte créé. Un administrateur doit vous attribuer un rôle. Redirection...';
        setTimeout(() => this.router.navigate(['/login']), 2500);
      },
      error: e => {
        this.loading = false;
        this.error = e?.error?.message || 'Erreur lors de la création du compte';
      },
    });
  }
}
