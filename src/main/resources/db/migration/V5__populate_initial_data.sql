-- 1. CIUDAD Y BARRIO
INSERT IGNORE INTO `cities` (`name`) VALUES ('Valencia');

-- Insertamos el barrio solo si no existe
INSERT IGNORE INTO `neighborhoods` (`name`, `street_name`, `postal_code`, `city_id`)
SELECT 'Mislata', 'Calle de Manuela Malasaña', '28004', id
FROM `cities`
WHERE `name` = 'Valencia'
LIMIT 1;

-- 2. USUARIO DE PRUEBA
INSERT IGNORE INTO `users` (`email`, `first_name`, `last_name`, `password`)
VALUES ('lucia@example.com', 'Lucía', 'García', '$2a$10$8.UnVuG9HHgffUDAlk8q7Ou5f2LvsS.g6izLbbZ.Z6098U1Cl6N7O');

-- 3. ROL PARA EL USUARIO
INSERT IGNORE INTO `user_roles` (`user_id`, `role`)
SELECT id, 'FAMILY' FROM `users` WHERE `email` = 'lucia@example.com';

-- 4. FAMILIA
INSERT IGNORE INTO `families` (`user_id`, `representative_name`, `family_name`, `description`, `neighborhood_id`)
SELECT u.id, 'Lucía García', 'García-López', 'Somos una familia con ganas de conocer vecinos.', n.id
FROM `users` u
JOIN `neighborhoods` n ON n.`name` = 'Mislata'
WHERE u.`email` = 'lucia@example.com';