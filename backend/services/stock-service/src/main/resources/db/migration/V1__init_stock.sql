-- ============================================================
-- stock-service : categories, fournisseurs, produits, mouvements
-- ============================================================

CREATE TABLE category (
    id          INTEGER PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description VARCHAR(255)
);

CREATE TABLE supplier (
    id                  INTEGER PRIMARY KEY,
    name                VARCHAR(255) NOT NULL,
    registration_number VARCHAR(50),
    phone               VARCHAR(50),
    email               VARCHAR(180)
);

CREATE TABLE product (
    id                 INTEGER       PRIMARY KEY,
    name               VARCHAR(255)  NOT NULL,
    description        VARCHAR(255),
    -- Quantite entiere : un stock ne se compte pas en fractions d'unite.
    available_quantity INTEGER       NOT NULL DEFAULT 0,
    price              NUMERIC(19,2),
    -- Colonne de verrouillage optimiste geree par JPA.
    version            BIGINT        NOT NULL DEFAULT 0,
    category_id        INTEGER       REFERENCES category (id),
    supplier_id        INTEGER       REFERENCES supplier (id),
    CONSTRAINT chk_product_quantity_positive CHECK (available_quantity >= 0)
);

CREATE INDEX idx_product_category ON product (category_id);
CREATE INDEX idx_product_supplier ON product (supplier_id);

CREATE TABLE stock_movement (
    id         INTEGER      PRIMARY KEY,
    type       VARCHAR(10)  NOT NULL,
    quantity   INTEGER      NOT NULL,
    note       VARCHAR(255),
    created_at TIMESTAMP    NOT NULL,
    product_id INTEGER      NOT NULL REFERENCES product (id) ON DELETE CASCADE,
    CONSTRAINT chk_movement_type CHECK (type IN ('ENTRY','EXIT'))
);

CREATE INDEX idx_movement_product ON stock_movement (product_id, created_at DESC);

-- Sequences Hibernate (strategie @GeneratedValue par defaut, increment de 50)
CREATE SEQUENCE category_seq       INCREMENT BY 50 START WITH 1;
CREATE SEQUENCE supplier_seq       INCREMENT BY 50 START WITH 1;
CREATE SEQUENCE product_seq        INCREMENT BY 50 START WITH 1;
CREATE SEQUENCE stock_movement_seq INCREMENT BY 50 START WITH 1;
