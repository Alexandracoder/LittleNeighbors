--- 1. Insertar Ciudad
 MERGE INTO cities (name) KEY (name) VALUES ('Valencia');

 -- 2. Insertar Neighborhood
 MERGE INTO neighborhoods (name, street_name, city_id)
 KEY (name)
 SELECT 'Sunnyvale', 'Main Street', id FROM cities WHERE name = 'Valencia';

 -- 3. Insertar Intereses
 MERGE INTO interests (name, type, icon) KEY (name) VALUES
 ('Soccer', 'SPORTS', 'soccer-ball'),
 ('Painting', 'ARTS', 'palette');

 -- 4. Insertar Usuarios
 MERGE INTO users (email, first_name, last_name, password)
 KEY (email)
 VALUES
 ('miller@test.com', 'Alexandra', 'Miller', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMG.7u.Y1WpW'),
 ('garcia@test.com', 'Carlos', 'Garcia', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMG.7u.Y1WpW');

 -- 5. Insertar roles para los usuarios
 MERGE INTO user_roles (user_id, role)
 KEY (user_id, role)
 SELECT u.id, 'USER' FROM users u WHERE u.email IN ('miller@test.com', 'garcia@test.com');

 -- 6. Insertar Familias
 MERGE INTO families (user_id, family_name, representative_name, description, neighborhood_id)
 KEY (user_id)
 SELECT u.id, 'Miller', 'Alexandra Miller', 'The creative Millers!', n.id
 FROM users u, neighborhoods n WHERE u.email = 'miller@test.com' AND n.name = 'Sunnyvale';

 MERGE INTO families (user_id, family_name, representative_name, description, neighborhood_id)
 KEY (user_id)
 SELECT u.id, 'Garcia', 'Carlos Garcia', 'Active family!', n.id
 FROM users u, neighborhoods n WHERE u.email = 'garcia@test.com' AND n.name = 'Sunnyvale';

 -- 7. Insertar Hijos
 -- Usamos un subselect seguro para evitar duplicados en pruebas
 INSERT INTO children (family_id, gender, birth_date, is_prenatal)
 SELECT f.id, 'BOY', '2021-06-15', false
 FROM families f WHERE f.family_name = 'Miller'
 AND NOT EXISTS (SELECT 1 FROM children c WHERE c.family_id = f.id);

 INSERT INTO children (family_id, gender, birth_date, is_prenatal)
 SELECT f.id, 'GIRL', '2022-02-20', false
 FROM families f WHERE f.family_name = 'Garcia'
 AND NOT EXISTS (SELECT 1 FROM children c WHERE c.family_id = f.id);