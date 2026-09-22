import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { IconComponent } from '../../components/icon/icon.component';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, IconComponent],
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.css'],
})
export class ForgotPasswordComponent {
  email = '';
  loading = false;
  sent = false;
  message = '';
  error = '';

  constructor(private auth: AuthService) {}

  /**
   * Le serveur ne renvoie plus ni jeton ni lien de reinitialisation : ceux-ci ne
   * transitent que par e-mail. L'ecran se contente donc d'afficher le message
   * generique, identique que le compte existe ou non.
   */
  send(): void {
    this.error = '';
    this.loading = true;
    this.auth.forgotPassword(this.email).subscribe({
      next: res => {
        this.loading = false;
        this.sent = true;
        this.message = res?.message || '';
      },
      error: e => {
        this.loading = false;
        this.error = e?.error?.message || "Erreur lors de l'envoi";
      },
    });
  }
}
