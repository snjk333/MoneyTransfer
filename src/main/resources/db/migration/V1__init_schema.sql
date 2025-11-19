-- V1__init_schema.sql

CREATE TABLE accounts (
                          id UUID PRIMARY KEY,
                          first_name VARCHAR(255) NOT NULL,
                          last_name VARCHAR(255) NOT NULL,
                          created_at TIMESTAMP NOT NULL,
                          updated_at TIMESTAMP NOT NULL
);

-- Таблица кошельков
CREATE TABLE wallets (
                         id UUID PRIMARY KEY,
                         number VARCHAR(20) NOT NULL UNIQUE,
                         balance NUMERIC(19, 4) NOT NULL,
                         currency VARCHAR(10) NOT NULL,
                         account_id UUID NOT NULL,
                         created_at TIMESTAMP NOT NULL,
                         updated_at TIMESTAMP NOT NULL,

                         CONSTRAINT fk_wallets_account FOREIGN KEY (account_id) REFERENCES accounts(id)
);

CREATE TABLE transactions (
                              id UUID PRIMARY KEY,
                              amount NUMERIC(19, 4) NOT NULL,
                              from_wallet_id UUID,
                              to_wallet_id UUID,
                              created_at TIMESTAMP NOT NULL,
                              updated_at TIMESTAMP NOT NULL,

                              CONSTRAINT fk_transactions_from FOREIGN KEY (from_wallet_id) REFERENCES wallets(id),
                              CONSTRAINT fk_transactions_to FOREIGN KEY (to_wallet_id) REFERENCES wallets(id)
);

CREATE INDEX idx_wallets_number ON wallets(number);
CREATE INDEX idx_wallets_account_id ON wallets(account_id);
CREATE INDEX idx_transactions_from ON transactions(from_wallet_id);
CREATE INDEX idx_transactions_to ON transactions(to_wallet_id);