-- =============================================================
--  Pay My Buddy - Données de test
--  Fichier : src/main/resources/data.sql
--  Mot de passe de tous les utilisateurs de test : "password123"
-- =============================================================

INSERT INTO users (username, email, password, balance) VALUES
    ('Alice Martin', 'alice@email.com', '$2b$10$vq77N2vbrm/sEb3w4xeEeOir.mCD6rxclev2dZGKAzzdK2lnW1wUS', 500.00),
    ('Bob Dupont',   'bob@email.com',   '$2b$10$vq77N2vbrm/sEb3w4xeEeOir.mCD6rxclev2dZGKAzzdK2lnW1wUS', 250.00),
    ('Clara Petit',  'clara@email.com', '$2b$10$vq77N2vbrm/sEb3w4xeEeOir.mCD6rxclev2dZGKAzzdK2lnW1wUS', 100.00),
    ('David Moreau', 'david@email.com', '$2b$10$vq77N2vbrm/sEb3w4xeEeOir.mCD6rxclev2dZGKAzzdK2lnW1wUS', 750.00);

-- Connexions entre utilisateurs
INSERT INTO user_connections (user_id, connection_id) VALUES
    (1, 2), (2, 1),  -- Alice <-> Bob
    (1, 3), (3, 1),  -- Alice <-> Clara
    (2, 4), (4, 2);  -- Bob   <-> David

-- Transactions de test
INSERT INTO transactions (sender_id, receiver_id, description, amount) VALUES
    (1, 2, 'Remboursement restaurant', 20.00),
    (2, 1, 'Part loyer vacances',     150.00),
    (1, 3, 'Cadeau anniversaire',      50.00),
    (4, 2, 'Courses communes',         35.50),
    (3, 1, 'Billet de concert',        45.00);