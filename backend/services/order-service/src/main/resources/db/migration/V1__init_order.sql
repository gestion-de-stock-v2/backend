-- ============================================================
-- order-service : commandes et lignes de commande
-- ============================================================
-- Ce schema etait auparavant recree par Hibernate a chaque demarrage
-- (ddl-auto: create), ce qui effacait toutes les commandes. Il est desormais
-- versionne par Flyway et Hibernate se contente de le valider.

CREATE TABLE customer_order (
    id                 INTEGER       PRIMARY KEY,
    reference          VARCHAR(100)  NOT NULL UNIQUE,
    total_amount       NUMERIC(19,2),
    payment_method     VARCHAR(30),
    customer_id        VARCHAR(100),
    created_date       TIMESTAMP     NOT NULL,
    last_modified_date TIMESTAMP
);

CREATE TABLE customer_line (
    id         INTEGER PRIMARY KEY,
    order_id   INTEGER REFERENCES customer_order (id) ON DELETE CASCADE,
    product_id INTEGER NOT NULL,
    quantity   INTEGER NOT NULL
);

CREATE INDEX idx_line_order ON customer_line (order_id);
CREATE INDEX idx_order_customer ON customer_order (customer_id);

CREATE SEQUENCE customer_order_seq INCREMENT BY 50 START WITH 1;
CREATE SEQUENCE customer_line_seq  INCREMENT BY 50 START WITH 1;
