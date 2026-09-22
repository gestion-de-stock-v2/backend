import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { LoginRequest, LoginResponse, RegisterRequest, Role, User } from '../models/user.model';

const TOKEN_KEY = 'gestionstock_token';
const USER_KEY = 'gestionstock_user';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private base = '/api/v1/auth';
  currentUser = signal<LoginResponse | null>(this.loadUser());

  constructor(private http: HttpClient, private router: Router) {}

  login(req: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.base}/login`, req).pipe(
      tap(res => {
        localStorage.setItem(TOKEN_KEY, res.token);
        localStorage.setItem(USER_KEY, JSON.stringify(res));
        this.currentUser.set(res);
      })
    );
  }

  /**
   * Inscription libre. Le role n'est pas transmis : le serveur attribue
   * systematiquement OBSERVATEUR (lecture seule). Seul un administrateur peut
   * ensuite elever les droits d'un compte.
   */
  register(data: RegisterRequest): Observable<User> {
    return this.http.post<User>(`${this.base}/register`, data);
  }

  forgotPassword(email: string): Observable<{ message: string }> {
    return this.http.post<{ message: string }>(`${this.base}/forgot-password`, { email });
  }

  resetPassword(token: string, newPassword: string): Observable<{ message: string }> {
    return this.http.post<{ message: string }>(`${this.base}/reset-password`, { token, newPassword });
  }

  changePassword(currentPassword: string, newPassword: string): Observable<{ message: string }> {
    return this.http.post<{ message: string }>(`${this.base}/change-password`, {
      currentPassword,
      newPassword,
    });
  }

  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    this.currentUser.set(null);
    this.router.navigate(['/login']);
  }

  getToken(): string | null { return localStorage.getItem(TOKEN_KEY); }
  isLoggedIn(): boolean { return !!this.getToken(); }
  getRole(): Role | null { return this.currentUser()?.role ?? null; }
  hasRole(...roles: Role[]): boolean {
    const r = this.getRole();
    return r !== null && roles.includes(r);
  }
  isAdmin(): boolean { return this.getRole() === 'ADMIN'; }

  private loadUser(): LoginResponse | null {
    const raw = localStorage.getItem(USER_KEY);
    if (!raw) return null;
    try {
      return JSON.parse(raw) as LoginResponse;
    } catch {
      // Entree corrompue : on repart d'une session vide plutot que de planter au demarrage.
      localStorage.removeItem(USER_KEY);
      return null;
    }
  }
}
