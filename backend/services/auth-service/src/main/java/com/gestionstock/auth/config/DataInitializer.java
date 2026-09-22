package com.gestionstock.auth.config;

import com.gestionstock.auth.model.Role;
import com.gestionstock.auth.model.User;
import com.gestionstock.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Comptes de demonstration, un par role.
 * <p>
 * Restreint au profil {@code dev} : ces identifiants sont publies dans le README et ne
 * doivent jamais exister ailleurs. Hors dev, c'est {@link AdminBootstrap} qui cree l'unique
 * compte administrateur, avec un mot de passe non devinable.
 */
@Component
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    @Override
    public void run(String... args) {
        create("admin",       "admin123!",       "Administrateur", "admin@gestionstock.com",       Role.ADMIN);
        create("gerant",      "gerant123!",      "Gerant",         "gerant@gestionstock.com",      Role.GERANT);
        create("magasinier",  "magasin123!",     "Magasinier",     "magasinier@gestionstock.com",  Role.MAGASINIER);
        create("vendeur",     "vendeur123!",     "Vendeur",        "vendeur@gestionstock.com",     Role.VENDEUR);
        create("acheteur",    "acheteur123!",    "Acheteur",       "acheteur@gestionstock.com",    Role.ACHETEUR);
        create("comptable",   "comptable123!",   "Comptable",      "comptable@gestionstock.com",   Role.COMPTABLE);
        create("observateur", "observateur123!", "Observateur",    "observateur@gestionstock.com", Role.OBSERVATEUR);
    }

    private void create(String username, String password, String name, String email, Role role) {
        if (userRepository.existsByUsername(username)) {
            return;
        }
        userRepository.save(User.builder()
                .username(username)
                .password(encoder.encode(password))
                .name(name)
                .email(email)
                .role(role)
                .active(true)
                .build());
        // Le mot de passe n'est pas journalise : il figure dans le README du profil dev.
        log.info("Compte de demonstration cree : {} ({})", username, role);
    }
}
