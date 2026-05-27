-- =============================================================
--  Pay My Buddy - Données de test
--  Fichier : src/main/resources/data.sql
--
--  Mot de passe de tous les utilisateurs :
--  password123
--
--  Hash BCrypt :
--  $2a$10$ou.uc/LCoESDY6jZtM9t4udEGGV7dJSk1mL1xVD66gjLxFi5x7Dly
-- =============================================================

-- -------------------------------------------------------------
--  Utilisateurs
-- -------------------------------------------------------------
INSERT IGNORE INTO users (
    username,
    email,
    password,
    balance,
    created_at
) VALUES
('Alice Martin','alice@email.com','$2a$10$ou.uc/LCoESDY6jZtM9t4udEGGV7dJSk1mL1xVD66gjLxFi5x7Dly',500.00,CURRENT_TIMESTAMP),
('Bob Dupont','bob@email.com','$2a$10$ou.uc/LCoESDY6jZtM9t4udEGGV7dJSk1mL1xVD66gjLxFi5x7Dly',250.00,CURRENT_TIMESTAMP),
('Clara Petit','clara@email.com','$2a$10$ou.uc/LCoESDY6jZtM9t4udEGGV7dJSk1mL1xVD66gjLxFi5x7Dly',100.00,CURRENT_TIMESTAMP),
('David Moreau','david@email.com','$2a$10$ou.uc/LCoESDY6jZtM9t4udEGGV7dJSk1mL1xVD66gjLxFi5x7Dly',750.00,CURRENT_TIMESTAMP);

-- -------------------------------------------------------------
--  Connexions utilisateurs
--  (relations symétriques)
-- -------------------------------------------------------------
INSERT IGNORE INTO user_connections (
    user_id,
    connection_id
) VALUES
(1, 2),(2, 1),
(1, 3),(3, 1),
(2, 4),(4, 2);

-- -------------------------------------------------------------
--  Transactions
-- -------------------------------------------------------------
INSERT IGNORE INTO transactions (
    sender_id,
    receiver_id,
    description,
    amount,
    created_at
) VALUES
(1,2,'Remboursement restaurant',20.00,CURRENT_TIMESTAMP),
(2,1,'Part loyer vacances',150.00,CURRENT_TIMESTAMP),
(1,3,'Cadeau anniversaire',50.00,CURRENT_TIMESTAMP),
(4,2,'Courses communes',35.50,CURRENT_TIMESTAMP),
(3,1,'Billet de concert',45.00,CURRENT_TIMESTAMP);