-- ============================================================
-- auth-service : utilisateurs, roles et jetons de reinitialisation
-- ============================================================

CREATE TABLE users (
    id        BIGSERIAL PRIMARY KEY,
    username  VARCHAR(50)  NOT NULL UNIQUE,
    password  VARCHAR(255) NOT NULL,
    name      VARCHAR(150),
    email     VARCHAR(180) UNIQUE,
    role      VARCHAR(20)  NOT NULL,
    active    BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT chk_users_role CHECK (role IN
        ('ADMIN','GERANT','MAGASINIER','VENDEUR','ACHETEUR','COMPTABLE','OBSERVATEUR'))
);

CREATE INDEX idx_users_email ON users (LOWER(email));

-- Seul le hachage SHA-256 du jeton est stocke (64 caracteres hexadecimaux) : une lecture
-- de la base ne permet pas de rejouer un lien de reinitialisation.
CREATE TABLE password_reset_token (
    id         BIGSERIAL PRIMARY KEY,
    token_hash VARCHAR(64) NOT NULL UNIQUE,
    user_id    BIGINT      NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    expires_at TIMESTAMP   NOT NULL,
    used       BOOLEAN     NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_prt_user ON password_reset_token (user_id);
