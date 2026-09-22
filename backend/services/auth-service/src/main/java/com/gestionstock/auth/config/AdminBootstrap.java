package com.gestionstock.auth.config;

import com.gestionstock.auth.model.Role;
import com.gestionstock.auth.model.User;
import com.gestionstock.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * Amorcage hors profil dev : cree l'unique compte administrateur si aucun n'existe.
 * <p>
 * Le mot de passe provient de {@code ADMIN_INITIAL_PASSWORD}. A defaut, il est tire
 * aleatoirement et affiche <strong>une seule fois</strong> au demarrage : il n'existe donc
 * aucun identifiant administrateur devinable en dehors du developpement.
 */
@Component
@Profile("!dev")
@RequiredArgsConstructor
@Slf4j
public class AdminBootstrap implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    @Value("${application.admin.username:admin}")
    private String adminUsername;

    @Value("${application.admin.email:admin@gestionstock.local}")
    private String adminEmail;

    @Value("${application.admin.initial-password:}")
    private String configuredPassword;

    @Override
    public void run(String... args) {
        if (userRepository.existsByUsername(adminUsername)) {
            return;
        }

        boolean generated = configuredPassword == null || configuredPassword.isBlank();
        String password = generated ? generatePassword() : configuredPassword;

        userRepository.save(User.builder()
                .username(adminUsername)
                .password(encoder.encode(password))
                .name("Administrateur")
                .email(adminEmail)
                .role(Role.ADMIN)
                .active(true)
                .build());

        if (generated) {
            log.warn("""

                    ============================================================
                    Compte administrateur initial cree : {}
                    Mot de passe genere : {}
                    Ce mot de passe ne sera plus jamais affiche. Changez-le des
                    la premiere connexion, ou definissez ADMIN_INITIAL_PASSWORD.
                    ============================================================""",
                    adminUsername, password);
        } else {
            log.info("Compte administrateur initial cree : {} (mot de passe fourni par configuration)",
                    adminUsername);
        }
    }

    private String generatePassword() {
        byte[] raw = new byte[24];
        new SecureRandom().nextBytes(raw);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(raw);
    }
}
