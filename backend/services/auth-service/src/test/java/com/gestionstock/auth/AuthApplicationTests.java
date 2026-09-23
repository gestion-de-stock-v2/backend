package com.gestionstock.auth;

import org.junit.jupiter.api.Test;

/**
 * Les tests d'integration necessitant Postgres et Kafka ne sont pas encore en place
 * (voir la section "Hors perimetre" du README). Ce test verifie uniquement le hachage
 * des jetons, qui est pur et sans dependance.
 */
class AuthApplicationTests {

    @Test
    void tokenHashIsDeterministicAndNotReversible() {
        String token = "un-jeton-de-test";
        String hash = com.gestionstock.auth.service.TokenHasher.sha256(token);

        org.junit.jupiter.api.Assertions.assertEquals(64, hash.length());
        org.junit.jupiter.api.Assertions.assertEquals(hash,
                com.gestionstock.auth.service.TokenHasher.sha256(token));
        org.junit.jupiter.api.Assertions.assertNotEquals(token, hash);
    }
}
