-- =============================================================
--  Pay My Buddy - Données de test
-- =============================================================

-- -------------------------------------------------------------
--  Nettoyage des données de test uniquement
-- -------------------------------------------------------------

DELETE FROM user_connections
    WHERE user_id IN (SELECT id FROM users WHERE email IN ('alice@email.com','bob@email.com','clara@email.com','david@email.com'))
        OR connection_id IN (SELECT id FROM users WHERE email IN ('alice@email.com','bob@email.com','clara@email.com','david@email.com'));

DELETE FROM transactions
    WHERE sender_id IN (SELECT id FROM users WHERE email IN ('alice@email.com','bob@email.com','clara@email.com','david@email.com'))
        OR receiver_id IN (SELECT id FROM users WHERE email IN ('alice@email.com','bob@email.com','clara@email.com','david@email.com'));

DELETE FROM users
    WHERE email IN ('alice@email.com','bob@email.com','clara@email.com','david@email.com');

-- -------------------------------------------------------------
--  Utilisateurs
-- -------------------------------------------------------------
INSERT INTO users (
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
-- -------------------------------------------------------------
INSERT INTO user_connections (
    user_id,
    connection_id
) VALUES
      (
          (SELECT id FROM users WHERE email='alice@email.com'),
          (SELECT id FROM users WHERE email='bob@email.com')
      ),
      (
          (SELECT id FROM users WHERE email='bob@email.com'),
          (SELECT id FROM users WHERE email='alice@email.com')
      ),
      (
          (SELECT id FROM users WHERE email='alice@email.com'),
          (SELECT id FROM users WHERE email='clara@email.com')
      ),
      (
          (SELECT id FROM users WHERE email='clara@email.com'),
          (SELECT id FROM users WHERE email='alice@email.com')
      ),
      (
          (SELECT id FROM users WHERE email='bob@email.com'),
          (SELECT id FROM users WHERE email='david@email.com')
      ),
      (
          (SELECT id FROM users WHERE email='david@email.com'),
          (SELECT id FROM users WHERE email='bob@email.com')
      );

-- -------------------------------------------------------------
--  Transactions
-- -------------------------------------------------------------
INSERT INTO transactions (
    sender_id,
    receiver_id,
    description,
    amount,
    created_at
) VALUES
      (
          (SELECT id FROM users WHERE email='alice@email.com'),
          (SELECT id FROM users WHERE email='bob@email.com'),
          'Remboursement restaurant',
          20.00,
          CURRENT_TIMESTAMP
      ),
      (
          (SELECT id FROM users WHERE email='bob@email.com'),
          (SELECT id FROM users WHERE email='alice@email.com'),
          'Part loyer vacances',
          150.00,
          CURRENT_TIMESTAMP
      ),
      (
          (SELECT id FROM users WHERE email='alice@email.com'),
          (SELECT id FROM users WHERE email='clara@email.com'),
          'Cadeau anniversaire',
          50.00,
          CURRENT_TIMESTAMP
      ),
      (
          (SELECT id FROM users WHERE email='david@email.com'),
          (SELECT id FROM users WHERE email='bob@email.com'),
          'Courses communes',
          35.50,
          CURRENT_TIMESTAMP
      ),
      (
          (SELECT id FROM users WHERE email='clara@email.com'),
          (SELECT id FROM users WHERE email='alice@email.com'),
          'Billet de concert',
          45.00,
          CURRENT_TIMESTAMP
      );