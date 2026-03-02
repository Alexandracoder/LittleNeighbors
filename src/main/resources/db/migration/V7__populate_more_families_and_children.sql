-- 1. ASEGURAR CIUDADES (Por si acaso no están)
INSERT INTO `cities` (`name`)
SELECT 'Valencia' WHERE NOT EXISTS (SELECT 1 FROM `cities` WHERE `name` = 'Valencia');
INSERT INTO `cities` (`name`)
SELECT 'Mislata' WHERE NOT EXISTS (SELECT 1 FROM `cities` WHERE `name` = 'Mislata');

-- 2. ASEGURAR BARRIOS DE VALENCIA Y MISLATA
-- Ruzafa (Valencia)
INSERT INTO `neighborhoods` (`name`, `street_name`, `postal_code`, `city_id`)
SELECT 'Ruzafa', 'Calle de Cádiz', '46006', id FROM `cities` WHERE `name` = 'Valencia'
AND NOT EXISTS (SELECT 1 FROM `neighborhoods` WHERE `name` = 'Ruzafa');

-- Benimaclet (Valencia)
INSERT INTO `neighborhoods` (`name`, `street_name`, `postal_code`, `city_id`)
SELECT 'Benimaclet', 'Calle del Barón de San Petrillo', '46020', id FROM `cities` WHERE `name` = 'Valencia'
AND NOT EXISTS (SELECT 1 FROM `neighborhoods` WHERE `name` = 'Benimaclet');

-- El Carmen (Valencia)
INSERT INTO `neighborhoods` (`name`, `street_name`, `postal_code`, `city_id`)
SELECT 'El Carmen', 'Calle de Quart', '46001', id FROM `cities` WHERE `name` = 'Valencia'
AND NOT EXISTS (SELECT 1 FROM `neighborhoods` WHERE `name` = 'El Carmen');

-- Mislata Centro
INSERT INTO `neighborhoods` (`name`, `street_name`, `postal_code`, `city_id`)
SELECT 'Mislata Centro', 'Avenida de Gregorio Gea', '46920', id FROM `cities` WHERE `name` = 'Mislata'
AND NOT EXISTS (SELECT 1 FROM `neighborhoods` WHERE `name` = 'Mislata Centro');

-- 3. NUEVOS USUARIOS (Password: password123)
INSERT INTO `users` (`email`, `first_name`, `last_name`, `password`)
VALUES
('pau.vlc@example.com', 'Pau', 'Navarro', '$2a$10$8.UnVuG9HHgffUDAlk8q7Ou5f2LvsS.g6izLbbZ.Z6098U1Cl6N7O'),
('amparo.mislata@example.com', 'Amparo', 'García', '$2a$10$8.UnVuG9HHgffUDAlk8q7Ou5f2LvsS.g6izLbbZ.Z6098U1Cl6N7O'),
('vicent.ruzafa@example.com', 'Vicent', 'Ferrer', '$2a$10$8.UnVuG9HHgffUDAlk8q7Ou5f2LvsS.g6izLbbZ.Z6098U1Cl6N7O');

-- 4. ASIGNAR ROLES
INSERT INTO `user_roles` (`user_id`, `role`)
SELECT id, 'FAMILY' FROM `users` WHERE `email` IN (
    'pau.vlc@example.com', 'amparo.mislata@example.com', 'vicent.ruzafa@example.com'
);

-- 5. NUEVAS FAMILIAS
-- Familia Navarro en Benimaclet
INSERT INTO `families` (`user_id`, `representative_name`, `family_name`, `description`, `neighborhood_id`)
SELECT u.id, 'Pau Navarro', 'Els Navarro', 'Vivim a Benimaclet i busquem grup de criança.', n.id
FROM `users` u, `neighborhoods` n WHERE u.`email` = 'pau.vlc@example.com' AND n.`name` = 'Benimaclet';

-- Familia García en Mislata
INSERT INTO `families` (`user_id`, `representative_name`, `family_name`, `description`, `neighborhood_id`)
SELECT u.id, 'Amparo García', 'Familia García-Mislata', 'Nos encanta pasear por el Parque de Cabecera.', n.id
FROM `users` u, `neighborhoods` n WHERE u.`email` = 'amparo.mislata@example.com' AND n.`name` = 'Mislata Centro';

-- 6. NUEVOS HIJOS (Usando tus nuevos Enums BOY/GIRL)
-- Hija de Els Navarro
INSERT INTO `children` (`birth_date`, `gender`, `family_id`)
SELECT '2020-05-15', 'GIRL', f.id FROM `families` f JOIN `users` u ON f.user_id = u.id WHERE u.email = 'pau.vlc@example.com';

-- Hijo de Amparo (Mislata)
INSERT INTO `children` (`birth_date`, `gender`, `family_id`)
SELECT '2022-11-20', 'BOY', f.id FROM `families` f JOIN `users` u ON f.user_id = u.id WHERE u.email = 'amparo.mislata@example.com';