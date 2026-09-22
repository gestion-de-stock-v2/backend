export type Role =
  | 'ADMIN' | 'GERANT' | 'MAGASINIER' | 'VENDEUR'
  | 'ACHETEUR' | 'COMPTABLE' | 'OBSERVATEUR';

export interface User {
  id?: number;
  username: string;
  name: string;
  email?: string;
  role: Role;
  active?: boolean;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  id: number;
  username: string;
  name: string;
  email: string;
  role: Role;
}

/**
 * Inscription libre : le role n'est volontairement pas transmis, il est impose
 * par le serveur (OBSERVATEUR). Son attribution releve d'un administrateur.
 */
export interface RegisterRequest {
  username: string;
  password: string;
  name: string;
  email: string;
}
