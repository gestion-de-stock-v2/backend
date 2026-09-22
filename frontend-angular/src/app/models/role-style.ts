import { Role } from './user.model';

/**
 * Apparence des rôles, définie une seule fois.
 *
 * Ces tables étaient auparavant recopiées dans quatre composants, avec des
 * valeurs hexadécimales divergentes et hors charte (un violet #9333ea n'existe
 * nulle part dans DESIGN.md). Tout passe désormais par les tokens.
 *
 * La charte ne propose que deux familles chromatiques (bleu, orange) plus les
 * neutres et deux teintes sémantiques : sept teintes franchement distinctes n'y
 * sont pas disponibles. La couleur seule ne peut donc pas porter la distinction
 * entre rôles — d'où l'icône, systématiquement affichée à côté du libellé.
 */
export const ROLE_COLORS: Record<Role, string> = {
  ADMIN:       'var(--danger)',
  GERANT:      'var(--primary-deep)',
  MAGASINIER:  'var(--accent)',
  VENDEUR:     'var(--success)',
  ACHETEUR:    'var(--primary-bright)',
  COMPTABLE:   'var(--accent-deep)',
  OBSERVATEUR: 'var(--ink-muted)',
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
