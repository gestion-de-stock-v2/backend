import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { switchMap } from 'rxjs';
import { Role, User } from '../../models/user.model';
import { ALL_ROLES, ROLE_COLORS, ROLE_ICONS } from '../../models/role-style';
import { IconComponent } from '../../components/icon/icon.component';
import { AuthService } from '../../services/auth.service';

interface NewUserForm {
  username: string;
  password: string;
  name: string;
  email: string;
  role: Role;
}

const EMPTY_FORM: NewUserForm = {
  username: '', password: '', name: '', email: '', role: 'OBSERVATEUR',
};

@Component({
  selector: 'app-usuarios',
  standalone: true,
  imports: [CommonModule, FormsModule, IconComponent],
  templateUrl: './usuarios.component.html',
  styleUrls: ['./usuarios.component.css'],
})
export class UsuariosComponent implements OnInit {
  private readonly usersUrl = '/api/v1/users';
  private readonly registerUrl = '/api/v1/auth/register';

  users: User[] = [];
  roles: Role[] = ALL_ROLES;

  roleIcons = ROLE_ICONS;

  roleColors = ROLE_COLORS;

  showForm = false;
  loading = false;
  error = '';
  success = '';
  showPassword = false;

  draft: NewUserForm = { ...EMPTY_FORM };

  constructor(private http: HttpClient, public auth: AuthService) {}

  ngOnInit(): void { this.load(); }

  load(): void {
    this.error = '';
    this.http.get<User[]>(this.usersUrl).subscribe({
      next: d => (this.users = d),
      error: e => (this.error = e?.error?.message || 'Erreur lors du chargement'),
    });
  }

  toggleForm(): void {
    this.showForm = !this.showForm;
    if (!this.showForm) this.resetForm();
  }

  resetForm(): void {
    this.draft = { ...EMPTY_FORM };
    this.error = '';
    this.success = '';
    this.showPassword = false;
  }

  /**
   * Creation en deux temps : /auth/register cree systematiquement un compte
   * OBSERVATEUR (le serveur ignore tout role fourni par le client), puis le role
   * demande est applique via l'endpoint reserve aux administrateurs.
   */
  create(): void {
    this.error = '';
    this.success = '';

    if (!this.draft.username.trim() || this.draft.username.length < 3) {
      this.error = "Nom d'utilisateur requis (3 caractères minimum)";
      return;
    }
    if (!this.draft.password || this.draft.password.length < 8) {
      this.error = 'Mot de passe requis (8 caractères minimum)';
      return;
    }
    if (!this.draft.name.trim()) {
      this.error = 'Nom complet requis';
      return;
    }
    if (!this.draft.email.trim()) {
      this.error = 'Email requis';
      return;
    }

    this.loading = true;
    const username = this.draft.username;
    const wantedRole = this.draft.role;

    this.http.post<User>(this.registerUrl, {
      username: this.draft.username,
      password: this.draft.password,
      name: this.draft.name,
      email: this.draft.email,
    }).pipe(
      switchMap(created =>
        wantedRole === 'OBSERVATEUR'
          ? [created]
          : this.http.put<User>(`${this.usersUrl}/${created.id}/role`, { role: wantedRole })
      )
    ).subscribe({
      next: () => {
        this.loading = false;
        this.success = `Utilisateur « ${username} » créé avec le rôle ${wantedRole}`;
        this.resetForm();
        this.showForm = false;
        this.load();
      },
      error: e => {
        this.loading = false;
        this.error = e?.error?.message || 'Erreur lors de la création';
      },
    });
  }

  updateRole(u: User, role: Role): void {
    if (!u.id) return;
    this.http.put<User>(`${this.usersUrl}/${u.id}/role`, { role }).subscribe({
      next: () => this.load(),
      error: e => (this.error = e?.error?.message || 'Erreur lors du changement de rôle'),
    });
  }

  toggleActive(u: User): void {
    if (!u.id) return;
    this.http.patch(`${this.usersUrl}/${u.id}/active`, {}).subscribe({
      next: () => this.load(),
      error: e => (this.error = e?.error?.message || 'Erreur'),
    });
  }

  remove(u: User): void {
    if (!u.id) return;
    if (u.username === this.auth.currentUser()?.username) {
      this.error = 'Vous ne pouvez pas supprimer votre propre compte';
      return;
    }
    if (confirm(`Supprimer définitivement l'utilisateur « ${u.username} » ?`)) {
      this.http.delete(`${this.usersUrl}/${u.id}`).subscribe({
        next: () => this.load(),
        error: e => (this.error = e?.error?.message || 'Erreur lors de la suppression'),
      });
    }
  }

  getRoleIcon(role: Role): string { return this.roleIcons[role]; }
  getRoleColor(role: Role): string { return this.roleColors[role]; }

  get activeCount(): number { return this.users.filter(u => u.active).length; }
  get inactiveCount(): number { return this.users.filter(u => !u.active).length; }
  countByRole(role: Role): number { return this.users.filter(u => u.role === role).length; }
}
