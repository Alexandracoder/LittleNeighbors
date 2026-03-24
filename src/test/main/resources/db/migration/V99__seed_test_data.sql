-- 1. Insertar Ciudad
INSERT IGNORE INTO cities (name) VALUES ('Valencia');

-- 2. Insertar Neighborhood
INSERT IGNORE INTO neighborhoods (name, street_name, city_id)
SELECT 'Sunnyvale', 'Main Street', id FROM cities WHERE name = 'Valencia';

-- 3. Insertar Intereses
INSERT IGNORE INTO interests (name, type, icon) VALUES
('Soccer', 'SPORTS', 'soccer-ball'),
('Painting', 'ARTS', 'palette');

-- 4. Insertar Usuarios
INSERT IGNORE INTO users (email, first_name, last_name, password)
VALUES
('miller@test.com', 'Alexandra', 'Miller', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMG.7u.Y1WpW'),
('garcia@test.com', 'Carlos', 'Garcia', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMG.7u.Y1WpW');

-- 5. Insertar roles para los usuarios
INSERT IGNORE INTO user_roles (user_id, role)
SELECT id, 'USER' FROM users WHERE email IN ('miller@test.com', 'garcia@test.com');

-- 6. Insertar Familias
INSERT IGNORE INTO families (user_id, family_name, representative_name, description, neighborhood_id)
SELECT u.id, 'Miller', 'Alexandra Miller', 'The creative Millers!', n.id
FROM users u, neighborhoods n WHERE u.email = 'miller@test.com' AND n.name = 'Sunnyvale';

INSERT IGNORE INTO families (user_id, family_name, representative_name, description, neighborhood_id)
SELECT u.id, 'Garcia', 'Carlos Garcia', 'Active family!', n.id
FROM users u, neighborhoods n WHERE u.email = 'garcia@test.com' AND n.name = 'Sunnyvale';

-- 7. Insertar Hijos
INSERT IGNORE INTO children (family_id, gender, birth_date, is_prenatal)
SELECT f.id, 'BOY', '2021-06-15', false
FROM families f WHERE f.family_name = 'Miller';

INSERT IGNORE INTO children (family_id, gender, birth_date, is_prenatal)
SELECT f.id, 'GIRL', '2022-02-20', false
FROM families f WHERE f.family_name = 'Garcia';