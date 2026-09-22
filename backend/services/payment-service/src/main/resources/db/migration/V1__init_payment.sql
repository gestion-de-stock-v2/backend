-- ============================================================
-- payment-service : paiements
-- ============================================================
-- Comme pour order-service, ce schema etait detruit et recree a chaque
-- demarrage (ddl-auto: create). Il est desormais versionne par Flyway.

CREATE TABLE payment (
    id                 INTEGER      PRIMARY KEY,
    amount             NUMERIC(19,2),
    payment_method     VARCHAR(30),
    order_id           INTEGER,
    created_date       TIMESTAMP    NOT NULL,
    last_modified_date TIMESTAMP
);

CREATE INDEX idx_payment_order ON payment (order_id);

CREATE SEQUENCE payment_seq INCREMENT BY 50 START WITH 1;
