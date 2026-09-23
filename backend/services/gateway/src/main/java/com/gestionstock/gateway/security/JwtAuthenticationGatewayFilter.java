package com.gestionstock.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Validation des jetons JWT au niveau de la passerelle.
 * <p>
 * La passerelle est le seul point d'entree expose : tout ce qui n'est pas explicitement
 * public doit presenter un jeton valide. Auparavant aucun service du systeme ne portait
 * de securite et l'ensemble des endpoints metier etait accessible sans authentification.
 * <p>
 * En cas de succes, l'identite est propagee en aval via {@code X-User-Name} et
 * {@code X-User-Role}. Ces en-tetes sont <strong>systematiquement effaces</strong> de la
 * requete entrante avant d'etre reecrits : sans cela, un client pourrait les fournir
 * lui-meme et usurper une identite aupres des services internes.
 */
@Component
@Slf4j
public class JwtAuthenticationGatewayFilter implements GlobalFilter, Ordered {

    public static final String USER_HEADER = "X-User-Name";
    public static final String ROLE_HEADER = "X-User-Role";

    /** Chemins accessibles sans jeton. */
    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/v1/auth/login",
            "/api/v1/auth/register",
            "/api/v1/auth/forgot-password",
            "/api/v1/auth/reset-password",
            "/actuator/health",
            "/actuator/info"
    );

    @Value("${jwt.secret}")
    private String secret;

    @PostConstruct
    void validateSecret() {
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException(
                    "jwt.secret est absent ou trop court : 32 octets minimum. "
                  + "Definissez la variable d'environnement JWT_SECRET (identique a auth-service).");
        }
    }

    private SecretKey key() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        if (isPublic(path)) {
            // Meme sur un chemin public, on interdit qu'un client injecte une identite.
            return chain.filter(exchange.mutate().request(stripIdentityHeaders(exchange)).build());
        }

        String authorization = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return unauthorized(exchange, "Jeton d'authentification absent");
        }

        try {
            Claims claims = Jwts.parser().verifyWith(key()).build()
                    .parseSignedClaims(authorization.substring(7))
                    .getPayload();

            String username = claims.getSubject();
            String role = claims.get("role", String.class);

            // Un jeton correctement signe mais depourvu de sujet ou de role ne permet
            // aucune decision d'autorisation en aval : il est refuse plutot que
            // propage avec un en-tete vide, que les services interpreteraient mal.
            if (username == null || username.isBlank() || role == null || role.isBlank()) {
                log.warn("Jeton valide mais incomplet (sujet ou role absent) sur {}", path);
                return unauthorized(exchange, "Jeton invalide ou expire");
            }

            ServerHttpRequest request = exchange.getRequest().mutate()
                    .headers(h -> {
                        h.remove(USER_HEADER);
                        h.remove(ROLE_HEADER);
                    })
                    .header(USER_HEADER, username)
                    .header(ROLE_HEADER, role)
                    .build();

            return chain.filter(exchange.mutate().request(request).build());

        } catch (Exception e) {
            log.debug("Jeton rejete sur {} : {}", path, e.getMessage());
            return unauthorized(exchange, "Jeton invalide ou expire");
        }
    }

    private ServerHttpRequest stripIdentityHeaders(ServerWebExchange exchange) {
        return exchange.getRequest().mutate()
                .headers(h -> {
                    h.remove(USER_HEADER);
                    h.remove(ROLE_HEADER);
                })
                .build();
    }

    private boolean isPublic(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");
        byte[] body = ("{\"status\":401,\"message\":\"" + message + "\"}")
                .getBytes(StandardCharsets.UTF_8);
        return exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(body)));
    }

    /** S'execute avant le routage afin de rejeter au plus tot. */
    @Override
    public int getOrder() {
        return -100;
    }
}
