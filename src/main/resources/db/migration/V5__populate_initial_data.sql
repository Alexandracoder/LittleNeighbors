-- 1. CIUDAD Y BARRIO (Requeridos para FamilyEntity)
INSERT INTO `cities` (`name`) VALUES ('Madrid');

INSERT INTO `neighborhoods` (`name`, `street_name`, `postal_code`, `city_id`)
SELECT 'Mislata', 'Calle de Manuela Malasaña', '28004', id FROM `cities` WHERE `name` = 'Madrid';

-- 2. USUARIO DE PRUEBA (Password: password123)
INSERT INTO `users` (`email`, `first_name`, `last_name`, `password`)
VALUES ('lucia@example.com', 'Lucía', 'García', '$2a$10$8.UnVuG9HHgffUDAlk8q7Ou5f2LvsS.g6izLbbZ.Z6098U1Cl6N7O');

-- 3. ROL PARA EL USUARIO (Tu tabla user_roles definida en V2)
INSERT INTO `user_roles` (`user_id`, `role`)
SELECT id, 'FAMILY' FROM `users` WHERE `email` = 'lucia@example.com';

-- 4. FAMILIA (Basado en el esquema V1 y tu FamilyEntity)
INSERT INTO `families` (`user_id`, `representative_name`, `family_name`, `description`, `neighborhood_id`)
SELECT u.id, 'Lucía García', 'García-López', 'Somos una familia con ganas de conocer vecinos.', n.id
FROM `users` u, `neighborhoods` n
WHERE u.`email` = 'lucia@example.com' AND n.`name` = 'Malasaña';