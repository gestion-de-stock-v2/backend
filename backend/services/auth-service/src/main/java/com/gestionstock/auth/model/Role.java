package com.gestionstock.auth.model;

/**
 * Roles metier de l'application. {@code OBSERVATEUR} est le role attribue par
 * defaut a toute inscription libre : il n'ouvre qu'un acces en lecture.
 */
public enum Role {
    ADMIN,
    GERANT,
    MAGASINIER,
    VENDEUR,
    ACHETEUR,
    COMPTABLE,
    OBSERVATEUR
}
