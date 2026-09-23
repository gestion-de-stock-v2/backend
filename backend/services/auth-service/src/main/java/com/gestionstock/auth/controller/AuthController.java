package com.gestionstock.auth.controller;

import com.gestionstock.auth.dto.*;
import com.gestionstock.auth.exception.BusinessException;
import com.gestionstock.auth.model.Role;
import com.gestionstock.auth.model.User;
import com.gestionstock.auth.repository.UserRepository;
import com.gestionstock.auth.security.JwtService;
import com.gestionstock.auth.service.PasswordService;
import com.gestionstock.auth.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    /** Role attribue a toute inscription libre : lecture seule. */
    private static final Role DEFAULT_ROLE = Role.OBSERVATEUR;

    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final PasswordService passwordService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessException("Identifiants invalides"));

        return ResponseEntity.ok(LoginResponse.builder()
                .token(jwtService.generateToken(user.getUsername(), user.getRole().name()))
                .id(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build());
    }

    /**
     * Inscription libre. Le role est <strong>impose par le serveur</strong> et n'est pas
     * lisible depuis la requete : un client ne peut plus s'auto-attribuer ADMIN sur cet
     * endpoint public. La montee en privilege passe par
     * {@code PUT /api/v1/users/{id}/role}, reserve aux ADMIN.
     */
    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@Valid @RequestBody RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("Nom d'utilisateur deja pris");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("E-mail deja utilise");
        }

        User user = userRepository.save(User.builder()
                .username(request.getUsername())
                .password(encoder.encode(request.getPassword()))
                .name(request.getName())
                .email(request.getEmail())
                .role(DEFAULT_ROLE)
                .active(true)
                .build());

        return ResponseEntity.ok(UserService.toDTO(user));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ForgotPasswordResponse> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok(passwordService.forgotPassword(request));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        passwordService.resetPassword(request);
        return ResponseEntity.ok(Map.of("message", "Mot de passe reinitialise avec succes"));
    }

    @PostMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request, Authentication auth) {
        passwordService.changePassword(auth.getName(), request);
        return ResponseEntity.ok(Map.of("message", "Mot de passe modifie avec succes"));
    }

    /** Consomme par le gateway et le frontend pour recharger le profil courant. */
    @GetMapping("/me")
    public ResponseEntity<UserDTO> me(Authentication auth) {
        return ResponseEntity.ok(UserService.toDTO(
                userRepository.findByUsername(auth.getName())
                        .orElseThrow(() -> new BusinessException("Utilisateur introuvable"))));
    }
}
