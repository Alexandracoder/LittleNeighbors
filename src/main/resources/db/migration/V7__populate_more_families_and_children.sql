-- 7. COMPLETAR FAMILIAS RESTANTES
-- Familia Ferrer en Ruzafa (Vicent ya estaba en la tabla users)
INSERT INTO `families` (`user_id`, `representative_name`, `family_name`, `description`, `neighborhood_id`)
SELECT u.id, 'Vicent Ferrer', 'Familia Ferrer-Ruzafa', 'Vivimos cerca del mercado y buscamos familias para planes de fin de semana.', n.id
FROM `users` u, `neighborhoods` n
WHERE u.`email` = 'vicent.ruzafa@example.com' AND n.`name` = 'Ruzafa';

-- Nueva Familia en El Carmen (Añadiendo usuario primero)
INSERT INTO `users` (`email`, `first_name`, `last_name`, `password`)
VALUES ('lucia.carmen@example.com', 'Lucía', 'Sanz', '$2a$10$8.UnVuG9HHgffUDAlk8q7Ou5f2LvsS.g6izLbbZ.Z6098U1Cl6N7O');

INSERT INTO `user_roles` (`user_id`, `role`)
SELECT id, 'FAMILY' FROM `users` WHERE `email` = 'lucia.carmen@example.com';

INSERT INTO `families` (`user_id`, `representative_name`, `family_name`, `description`, `neighborhood_id`)
SELECT u.id, 'Lucía Sanz', 'Els del Carmen', 'Amantes de las fallas y de los paseos por el río Turia.', n.id
FROM `users` u, `neighborhoods` n
WHERE u.`email` = 'lucia.carmen@example.com' AND n.`name` = 'El Carmen';

-- 8. MÁS HIJOS (Respetando BOY/GIRL)
-- Hijos de la Familia Ferrer (Ruzafa)
INSERT INTO `children` (`birth_date`, `gender`, `family_id`)
SELECT '2018-09-12', 'BOY', f.id FROM `families` f JOIN `users` u ON f.user_id = u.id WHERE u.email = 'vicent.ruzafa@example.com';

INSERT INTO `children` (`birth_date`, `gender`, `family_id`)
SELECT '2021-02-28', 'GIRL', f.id FROM `families` f JOIN `users` u ON f.user_id = u.id WHERE u.email = 'vicent.ruzafa@example.com';

-- Hijo de Lucía (El Carmen)
INSERT INTO `children` (`birth_date`, `gender`, `family_id`)
SELECT '2023-06-10', 'BOY', f.id FROM `families` f JOIN `users` u ON f.user_id = u.id WHERE u.email = 'lucia.carmen@example.com';