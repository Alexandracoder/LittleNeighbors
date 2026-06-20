-- V13__populate_valencia_neighborhoods.sql

-- 1. Eixample & Gran Vía
INSERT INTO neighborhoods (name, street_name, postal_code, city_id)
SELECT 'Eixample', 'Gran Vía del Marqués del Turia', '46005', id FROM cities WHERE name = 'Valencia'
AND NOT EXISTS (SELECT 1 FROM neighborhoods WHERE name = 'Eixample');

-- 2. Extramurs
INSERT INTO neighborhoods (name, street_name, postal_code, city_id)
SELECT 'Extramurs', 'Gran Vía de Fernando el Católico', '46008', id FROM cities WHERE name = 'Valencia'
AND NOT EXISTS (SELECT 1 FROM neighborhoods WHERE name = 'Extramurs');

-- 3. L'Olivereta
INSERT INTO neighborhoods (name, street_name, postal_code, city_id)
SELECT 'L Olivereta', 'Calle del Cid', '46018', id FROM cities WHERE name = 'Valencia'
AND NOT EXISTS (SELECT 1 FROM neighborhoods WHERE name = 'L Olivereta');

-- 4. La Saïdia
INSERT INTO neighborhoods (name, street_name, postal_code, city_id)
SELECT 'La Saidia', 'Calle de Sagunto', '46009', id FROM cities WHERE name = 'Valencia'
AND NOT EXISTS (SELECT 1 FROM neighborhoods WHERE name = 'La Saidia');

-- 5. El Pla del Real
INSERT INTO neighborhoods (name, street_name, postal_code, city_id)
SELECT 'El Pla del Real', 'Avenida de Blasco Ibáñez', '46010', id FROM cities WHERE name = 'Valencia'
AND NOT EXISTS (SELECT 1 FROM neighborhoods WHERE name = 'El Pla del Real');

-- 6. Algirós (Zona Cedro/Universidades)
INSERT INTO neighborhoods (name, street_name, postal_code, city_id)
SELECT 'Algiros', 'Plaza del Cedro', '46022', id FROM cities WHERE name = 'Valencia'
AND NOT EXISTS (SELECT 1 FROM neighborhoods WHERE name = 'Algiros');

-- 7. Benicalap
INSERT INTO neighborhoods (name, street_name, postal_code, city_id)
SELECT 'Benicalap', 'Avenida de Burjasot', '46025', id FROM cities WHERE name = 'Valencia'
AND NOT EXISTS (SELECT 1 FROM neighborhoods WHERE name = 'Benicalap');

-- 8. Quatre Carreres (Cerca de las Artes y las Ciencias)
INSERT INTO neighborhoods (name, street_name, postal_code, city_id)
SELECT 'Quatre Carreres', 'Avenida de la Plata', '46006', id FROM cities WHERE name = 'Valencia'
AND NOT EXISTS (SELECT 1 FROM neighborhoods WHERE name = 'Quatre Carreres');