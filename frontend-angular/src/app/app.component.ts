import { Component, signal } from '@angular/core';
import { NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { filter } from 'rxjs';
import { AuthService } from './services/auth.service';
import { HeaderComponent } from './pages/header/header.component';
import { ThemeToggleComponent } from './components/theme-toggle/theme-toggle.component';

/** Routes servies en pleine page, sans menu latéral ni barre supérieure. */
const PUBLIC_ROUTES = ['/', '/login', '/signup', '/forgot-password', '/reset-password', '/404'];

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, CommonModule, HeaderComponent, ThemeToggleComponent],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent {
  /** Vrai sur les écrans publics : la coque applicative est alors masquée. */
  isPublic = signal(true);

  /** Ouverture du tiroir de navigation sur mobile. */
  navOpen = signal(false);

  constructor(public auth: AuthService, private router: Router) {
    this.evaluate(this.router.url);
    this.router.events
      .pipe(filter((e): e is NavigationEnd => e instanceof NavigationEnd))
      .subscribe(e => {
        this.evaluate(e.urlAfterRedirects);
        this.navOpen.set(false); // toute navigation referme le tiroir
      });
  }

  private evaluate(url: string): void {
    const path = url.split('?')[0].split('#')[0];
    this.isPublic.set(PUBLIC_ROUTES.includes(path) || !this.auth.isLoggedIn());
  }

  toggleNav(): void { this.navOpen.update(v => !v); }
}
