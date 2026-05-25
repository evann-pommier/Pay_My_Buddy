-- =============================================================
--  Pay My Buddy - Schéma de base de données
--  Compatible MySQL 8+
--
--  Note : Les contraintes "pas de virement vers soi-même" et
--  "pas de connexion vers soi-même" sont gérées dans la couche
--  service Java (limitation MySQL : CHECK incompatible avec FK
--  ON DELETE/UPDATE CASCADE).
-- =============================================================

-- -------------------------------------------------------------
--  Suppression des tables dans l'ordre inverse des dépendances
-- -------------------------------------------------------------
DROP TABLE IF EXISTS user_connections;
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS users;

-- -------------------------------------------------------------
--  Table : users
-- -------------------------------------------------------------
CREATE TABLE users (
    id       INT             NOT NULL AUTO_INCREMENT,
    username VARCHAR(100)    NOT NULL,
    email    VARCHAR(255)    NOT NULL,
    password VARCHAR(255)    NOT NULL,
    balance  DECIMAL(10, 2)  NOT NULL DEFAULT 0.00,
    created_at TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_users            PRIMARY KEY (id),
    CONSTRAINT uq_users_email      UNIQUE (email),
    CONSTRAINT uq_users_username   UNIQUE (username),
    CONSTRAINT ck_balance_positive CHECK (balance >= 0)
);

-- -------------------------------------------------------------
--  Table : transactions
-- -------------------------------------------------------------
CREATE TABLE transactions (
    id          INT             NOT NULL AUTO_INCREMENT,
    sender_id   INT             NOT NULL,
    receiver_id INT             NOT NULL,
    description VARCHAR(255),
    amount      DECIMAL(10, 2)  NOT NULL,
    created_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
 
    CONSTRAINT pk_transactions          PRIMARY KEY (id),
    CONSTRAINT fk_transactions_sender   FOREIGN KEY (sender_id)
        REFERENCES users (id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_transactions_receiver FOREIGN KEY (receiver_id)
        REFERENCES users (id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT ck_amount_positive       CHECK (amount > 0)
);

-- -------------------------------------------------------------
--  Table : user_connections
-- -------------------------------------------------------------
CREATE TABLE user_connections (
    user_id       INT NOT NULL,
    connection_id INT NOT NULL,
 
    CONSTRAINT pk_user_connections       PRIMARY KEY (user_id, connection_id),
    CONSTRAINT fk_connections_user       FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_connections_connection FOREIGN KEY (connection_id)
        REFERENCES users (id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- -------------------------------------------------------------
--  Index pour accélérer les requêtes fréquentes
-- -------------------------------------------------------------
CREATE INDEX idx_users_email           ON users (email);
CREATE INDEX idx_users_username        ON users (username);
CREATE INDEX idx_transactions_sender   ON transactions (sender_id);
CREATE INDEX idx_transactions_receiver ON transactions (receiver_id);
CREATE INDEX idx_connections_user      ON user_connections (user_id);