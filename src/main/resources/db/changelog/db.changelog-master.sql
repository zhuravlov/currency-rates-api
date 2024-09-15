-- liquibase formatted sql

-- changeset liquibase:1

CREATE TABLE observable_currencies
(
    id            SERIAL PRIMARY KEY,
    currency_code VARCHAR(3) UNIQUE NOT NULL,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE exchange_rates
(
    id                   SERIAL PRIMARY KEY,
    base_currency_code   VARCHAR(3)     NOT NULL,
    target_currency_code VARCHAR(3)     NOT NULL,
    rate                 DECIMAL(15, 6) NOT NULL,
    traded_at            TIMESTAMP      NOT NULL,
    created_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
