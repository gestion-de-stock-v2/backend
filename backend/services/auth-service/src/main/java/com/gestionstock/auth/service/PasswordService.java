package com.gestionstock.auth.service;

import com.gestionstock.auth.dto.*;
import com.gestionstock.auth.exception.BusinessException;
import com.gestionstock.auth.exception.ResourceNotFoundException;
import com.gestionstock.auth.kafka.PasswordResetNotification;
import com.gestionstock.auth.kafka.PasswordResetProducer;
import com.gestionstock.auth.model.PasswordResetToken;
import com.gestionstock.auth.model.User;
import com.gestionstock.auth.repository.PasswordResetTokenRepository;
import com.gestionstock.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordService {

    private static final int EXPIRY_MINUTES = 30;
    private static final String GENERIC_MESSAGE =
            "Si un compte existe avec cet e-mail, vous recevrez un lien de reinitialisation.";

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder encoder;
    private final PasswordResetProducer passwordResetProducer;
    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${application.frontend-url:http://localhost:4200}")
    private String frontendUrl;

    /**
     * Le jeton n'est <strong>jamais</strong> renvoye au client : il part uniquement par e-mail,
     * via un evenement Kafka consomme par notification-service. La reponse est identique que
     * le compte existe ou non, ce qui empeche l'enumeration des comptes.
     */
    @Transactional
    public ForgotPasswordResponse forgotPassword(ForgotPasswordRequest request) {
        userRepository.findByEmailIgnoreCase(request.getEmail()).ifPresent(user -> {
            tokenRepository.deleteByUserId(user.getId());

            byte[] raw = new byte[32];
            secureRandom.nextBytes(raw);
            String token = Base64.getUrlEncoder().withoutPadding().encodeToString(raw);

            tokenRepository.save(PasswordResetToken.builder()
                    .tokenHash(TokenHasher.sha256(token))
                    .user(user)
                    .expiresAt(LocalDateTime.now().plusMinutes(EXPIRY_MINUTES))
                    .used(false)
                    .build());

            passwordResetProducer.sendPasswordResetLink(new PasswordResetNotification(
                    user.getEmail(),
                    user.getUsername(),
                    frontendUrl + "/reset-password?token=" + token,
                    EXPIRY_MINUTES
            ));
        });

        return ForgotPasswordResponse.builder().message(GENERIC_MESSAGE).build();
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken prt = tokenRepository.findByTokenHash(TokenHasher.sha256(request.getToken()))
                .orElseThrow(() -> new BusinessException("Lien de reinitialisation invalide"));

        if (Boolean.TRUE.equals(prt.getUsed())) {
            throw new BusinessException("Ce lien a deja ete utilise");
        }
        if (prt.isExpired()) {
            throw new BusinessException("Ce lien a expire. Veuillez en demander un nouveau.");
        }

        User user = prt.getUser();
        user.setPassword(encoder.encode(request.getNewPassword()));
        userRepository.save(user);

        prt.setUsed(true);
        tokenRepository.save(prt);
        log.info("Mot de passe reinitialise pour l'utilisateur id={}", user.getId());
    }

    @Transactional
    public void changePassword(String username, ChangePasswordRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

        if (!encoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BusinessException("Mot de passe actuel incorrect");
        }
        if (encoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new BusinessException("Le nouveau mot de passe doit etre different de l'ancien");
        }

        user.setPassword(encoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
