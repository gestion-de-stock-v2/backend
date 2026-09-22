package com.gestionstock.gateway.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifie que la passerelle refuse tout acces non authentifie et qu'une identite ne peut
 * pas etre injectee par le client. C'est le point de controle unique du systeme : avant,
 * aucun service ne portait de securite.
 */
class JwtAuthenticationGatewayFilterTest {

    private static final String SECRET = "un-secret-de-test-suffisamment-long-pour-hs256!!";

    private JwtAuthenticationGatewayFilter filter;

    @BeforeEach
    void setUp() {
        filter = new JwtAuthenticationGatewayFilter();
        ReflectionTestUtils.setField(filter, "secret", SECRET);
    }

    private String validToken(String username, String role) {
        return Jwts.builder()
                .claims(Map.of("role", role))
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    /** Chaine qui capture l'echange transmis en aval, et ne fait rien d'autre. */
    private GatewayFilterChain capturingChain(AtomicReference<ServerWebExchange> sink) {
        return exchange -> {
            sink.set(exchange);
            return Mono.empty();
        };
    }

    @Test
    void rejectsRequestWithoutToken() {
        var exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/api/v1/products").build());

        filter.filter(exchange, e -> Mono.empty()).block();

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }

    /**
     * Jeton signe avec une autre cle : c'est le scenario d'un attaquant qui forge son propre
     * jeton sans connaitre le secret du serveur.
     */
    @Test
    void rejectsTokenSignedWithAnotherSecret() {
        String foreign = Jwts.builder()
                .claims(Map.of("role", "ADMIN"))
                .subject("pirate")
                .expiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(Keys.hmacShaKeyFor(
                        "une-tout-autre-cle-de-32-octets-minimum-ok!!".getBytes(StandardCharsets.UTF_8)))
                .compact();

        var exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/api/v1/products")
                .header("Authorization", "Bearer " + foreign)
                .build());

        filter.filter(exchange, e -> Mono.empty()).block();

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }

    /**
     * Charge utile reecrite pour s'attribuer le role ADMIN, en conservant la signature
     * d'origine. La signature ne correspond plus au contenu : le jeton doit etre rejete.
     */
    @Test
    void rejectsTokenWithEscalatedPayload() {
        String[] parts = validToken("alice", "OBSERVATEUR").split("\\.");
        String payload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
        String escalated = payload.replace("OBSERVATEUR", "ADMIN");
        String forged = parts[0] + "."
                + Base64.getUrlEncoder().withoutPadding()
                        .encodeToString(escalated.getBytes(StandardCharsets.UTF_8))
                + "." + parts[2];

        var exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/api/v1/products")
                .header("Authorization", "Bearer " + forged)
                .build());

        filter.filter(exchange, e -> Mono.empty()).block();

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }

    @Test
    void rejectsExpiredToken() {
        String expired = Jwts.builder()
                .claims(Map.of("role", "ADMIN"))
                .subject("alice")
                .issuedAt(new Date(System.currentTimeMillis() - 120_000))
                .expiration(new Date(System.currentTimeMillis() - 60_000))
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
                .compact();

        var exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/api/v1/products")
                .header("Authorization", "Bearer " + expired)
                .build());

        filter.filter(exchange, e -> Mono.empty()).block();

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }

    @Test
    void allowsPublicAuthEndpointsWithoutToken() {
        var sink = new AtomicReference<ServerWebExchange>();
        var exchange = MockServerWebExchange.from(
                MockServerHttpRequest.post("/api/v1/auth/login").build());

        filter.filter(exchange, capturingChain(sink)).block();

        assertNotNull(sink.get(), "la requete doit etre transmise en aval");
        assertNotEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }

    @Test
    void propagatesIdentityFromValidToken() {
        var sink = new AtomicReference<ServerWebExchange>();
        var exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/api/v1/products")
                .header("Authorization", "Bearer " + validToken("alice", "GERANT"))
                .build());

        filter.filter(exchange, capturingChain(sink)).block();

        var headers = sink.get().getRequest().getHeaders();
        assertEquals("alice", headers.getFirst(JwtAuthenticationGatewayFilter.USER_HEADER));
        assertEquals("GERANT", headers.getFirst(JwtAuthenticationGatewayFilter.ROLE_HEADER));
    }

    /**
     * Un client ne doit pas pouvoir se declarer ADMIN en fournissant lui-meme les en-tetes
     * d'identite : la passerelle les ecrase toujours avec le contenu du jeton verifie.
     */
    @Test
    void clientSuppliedIdentityHeadersAreOverwritten() {
        var sink = new AtomicReference<ServerWebExchange>();
        var exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/api/v1/products")
                .header("Authorization", "Bearer " + validToken("alice", "OBSERVATEUR"))
                .header(JwtAuthenticationGatewayFilter.USER_HEADER, "root")
                .header(JwtAuthenticationGatewayFilter.ROLE_HEADER, "ADMIN")
                .build());

        filter.filter(exchange, capturingChain(sink)).block();

        var headers = sink.get().getRequest().getHeaders();
        assertEquals("alice", headers.getFirst(JwtAuthenticationGatewayFilter.USER_HEADER));
        assertEquals("OBSERVATEUR", headers.getFirst(JwtAuthenticationGatewayFilter.ROLE_HEADER));
    }

    /** Meme sur un chemin public, une identite injectee par le client doit disparaitre. */
    @Test
    void identityHeadersAreStrippedOnPublicPaths() {
        var sink = new AtomicReference<ServerWebExchange>();
        var exchange = MockServerWebExchange.from(MockServerHttpRequest.post("/api/v1/auth/login")
                .header(JwtAuthenticationGatewayFilter.USER_HEADER, "root")
                .header(JwtAuthenticationGatewayFilter.ROLE_HEADER, "ADMIN")
                .build());

        filter.filter(exchange, capturingChain(sink)).block();

        var headers = sink.get().getRequest().getHeaders();
        assertNull(headers.getFirst(JwtAuthenticationGatewayFilter.USER_HEADER));
        assertNull(headers.getFirst(JwtAuthenticationGatewayFilter.ROLE_HEADER));
    }
}
