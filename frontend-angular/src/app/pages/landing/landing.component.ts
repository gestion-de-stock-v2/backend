import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { IconComponent } from '../../components/icon/icon.component';
import { ThemeToggleComponent } from '../../components/theme-toggle/theme-toggle.component';

interface Feature {
  icon: string;
  title: string;
  text: string;
}

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [CommonModule, RouterLink, IconComponent, ThemeToggleComponent],
  templateUrl: './landing.component.html',
  styleUrls: ['./landing.component.css'],
})
export class LandingComponent {
  constructor(public auth: AuthService) {}

  features: Feature[] = [
    {
      icon: 'box',
      title: 'Catalogue et stock unifiés',
      text: "Produits, catégories et fournisseurs dans un référentiel unique. Une seule quantité disponible, jamais deux chiffres contradictoires.",
    },
    {
      icon: 'swap',
      title: 'Mouvements tracés',
      text: "Chaque entrée et chaque sortie est historisée avec son auteur, sa date et le stock résultant. L'inventaire se reconstitue à tout instant.",
    },
    {
      icon: 'alert',
      title: 'Alertes de rupture',
      text: 'Les seuils faibles remontent en tête de tableau de bord, avant que la rupture ne bloque une commande client.',
    },
    {
      icon: 'cart',
      title: 'Commandes et paiements',
      text: "De la commande client au paiement confirmé, avec décrément de stock garanti contre la survente et compensation en cas d'échec.",
    },
    {
      icon: 'shield',
      title: 'Sept rôles, droits réels',
      text: "Administrateur, gérant, magasinier, vendeur, acheteur, comptable, observateur. Les droits sont vérifiés côté serveur, pas seulement masqués à l'écran.",
    },
    {
      icon: 'mail',
      title: 'Notifications automatiques',
      text: 'Confirmation de commande, confirmation de paiement et réinitialisation de mot de passe partent par e-mail sans intervention.',
    },
  ];

  stats = [
    { value: '9',  label: 'services indépendants' },
    { value: '7',  label: 'rôles métier' },
    { value: '1',  label: 'commande pour tout démarrer' },
  ];
}
