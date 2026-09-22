import { Role } from './user.model';

/**
 * Apparence des rôles, définie une seule fois.
 *
 * Ces tables étaient auparavant recopiées dans quatre composants, avec des
 * valeurs hexadécimales divergentes et hors charte. Tout passe désormais par
 * les tokens du spectre des rôles, documenté dans DESIGN.md § Role Spectrum :
 * sept teintes séparées d'au moins 30° sur la roue, chacune lisible sur le fond
 * teinté de la pastille (≥ 4.5:1 en clair comme en sombre).
 *
 * L'icône reste affichée à côté du libellé : la couleur ne doit jamais être le
 * seul vecteur de sens, pour les déficiences de vision des couleurs comme pour
 * l'impression en niveaux de gris.
 *
 * Toute modification de ces teintes doit repasser `tools/check-palette.py`.
 */
export const ROLE_COLORS: Record<Role, string> = {
  ADMIN:       'var(--role-admin)',
  GERANT:      'var(--role-manager)',
  MAGASINIER:  'var(--role-warehouse)',
  VENDEUR:     'var(--role-sales)',
  ACHETEUR:    'var(--role-purchasing)',
  COMPTABLE:   'var(--role-accounting)',
  OBSERVATEUR: 'var(--role-observer)',
};

export const ROLE_ICONS: Record<Role, string> = {
  ADMIN:       'crown',
  GERANT:      'briefcase',
  MAGASINIER:  'package',
  VENDEUR:     'cart',
  ACHETEUR:    'shoppingBag',
  COMPTABLE:   'chart',
  OBSERVATEUR: 'eye',
};

export const ALL_ROLES: Role[] = [
  'ADMIN', 'GERANT', 'MAGASINIER', 'VENDEUR', 'ACHETEUR', 'COMPTABLE', 'OBSERVATEUR',
];
