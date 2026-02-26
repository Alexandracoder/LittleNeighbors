-- 1. ASEGURAR BARRIOS ADICIONALES (Por si no estaban en el V5)
INSERT INTO `neighborhoods` (`name`, `street_name`, `postal_code`, `city_id`)
SELECT 'Chamberí', 'Calle de Fuencarral', '28010', id FROM `cities` WHERE `name` = 'Madrid'
AND NOT EXISTS (SELECT 1 FROM `neighborhoods` WHERE `name` = 'Chamberí');

INSERT INTO `neighborhoods` (`name`, `street_name`, `postal_code`, `city_id`)
SELECT 'Salamanca', 'Calle de Serrano', '28001', id FROM `cities` WHERE `name` = 'Madrid'
AND NOT EXISTS (SELECT 1 FROM `neighborhoods` WHERE `name` = 'Salamanca');

-- 2. NUEVOS USUARIOS (Password: password123)
INSERT INTO `users` (`email`, `first_name`, `last_name`, `password`)
VALUES
('marcos.ruiz@example.com', 'Marcos', 'Ruiz', '$2a$10$8.UnVuG9HHgffUDAlk8q7Ou5f2LvsS.g6izLbbZ.Z6098U1Cl6N7O'),
('elena.vazquez@example.com', 'Elena', 'Vázquez', '$2a$10$8.UnVuG9HHgffUDAlk8q7Ou5f2LvsS.g6izLbbZ.Z6098U1Cl6N7O'),
('sergio.perez@example.com', 'Sergio', 'Pérez', '$2a$10$8.UnVuG9HHgffUDAlk8q7Ou5f2LvsS.g6izLbbZ.Z6098U1Cl6N7O'),
('clara.bosch@example.com', 'Clara', 'Bosch', '$2a$10$8.UnVuG9HHgffUDAlk8q7Ou5f2LvsS.g6izLbbZ.Z6098U1Cl6N7O');

-- 3. ASIGNAR ROLES
INSERT INTO `user_roles` (`user_id`, `role`)
SELECT id, 'FAMILY' FROM `users` WHERE `email` IN (
    'marcos.ruiz@example.com', 'elena.vazquez@example.com',
    'sergio.perez@example.com', 'clara.bosch@example.com'
);

-- 4. NUEVAS FAMILIAS
-- Familia Ruiz en Chamberí
INSERT INTO `families` (`user_id`, `representative_name`, `family_name`, `description`, `neighborhood_id`)
SELECT u.id, 'Marcos Ruiz', 'Familia Ruiz-Sanz', 'Nos gusta el senderismo y buscamos otras familias para excursiones.', n.id
FROM `users` u, `neighborhoods` n WHERE u.`email` = 'marcos.ruiz@example.com' AND n.`name` = 'Chamberí';

-- Familia Vázquez en Retiro
INSERT INTO `families` (`user_id`, `representative_name`, `family_name`, `description`, `neighborhood_id`)
SELECT u.id, 'Elena Vázquez', 'Los Vázquez', 'Recién llegados al barrio. Tenemos una hija de 4 años.', n.id
FROM `users` u, `neighborhoods` n WHERE u.`email` = 'elena.vazquez@example.com' AND n.`name` = 'Retiro';

-- Familia Pérez en Salamanca
INSERT INTO `families` (`user_id`, `representative_name`, `family_name`, `description`, `neighborhood_id`)
SELECT u.id, 'Sergio Pérez', 'Pérez-Gómez', 'Buscamos grupo de juegos para las tardes en el parque.', n.id
FROM `users` u, `neighborhoods` n WHERE u.`email` = 'sergio.perez@example.com' AND n.`name` = 'Salamanca';

-- 5. NUEVOS HIJOS (Varios por familia en algunos casos)
-- Hijos de Familia Ruiz (2 hijos)
INSERT INTO `children` (`birth_date`, `gender`, `family_id`)
SELECT '2019-03-10', 'MALE', f.id FROM `families` f JOIN `users` u ON f.user_id = u.id WHERE u.email = 'marcos.ruiz@example.com';
INSERT INTO `children` (`birth_date`, `gender`, `family_id`)
SELECT '2021-08-22', 'FEMALE', f.id FROM `families` f JOIN `users` u ON f.user_id = u.id WHERE u.email = 'marcos.ruiz@example.com';

-- Hija de Familia Vázquez
INSERT INTO `children` (`birth_date`, `gender`, `family_id`)
SELECT '2020-01-05', 'FEMALE', f.id FROM `families` f JOIN `users` u ON f.user_id = u.id WHERE u.email = 'elena.vazquez@example.com';

-- Hijo de Familia Pérez
INSERT INTO `children` (`birth_date`, `gender`, `family_id`)
SELECT '2022-12-12', 'MALE', f.id FROM `families` f JOIN `users` u ON f.user_id = u.id WHERE u.email = 'sergio.perez@example.com';