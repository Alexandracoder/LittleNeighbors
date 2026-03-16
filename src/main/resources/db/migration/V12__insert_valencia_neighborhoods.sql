-- V12__insert_valencia_neighborhoods.sql
-- Insertar barrios de Valencia vinculados a la ciudad ID 1 (Valencia)

INSERT INTO neighborhoods (name, street_name, postal_code, city_id)
SELECT 'Ciutat Vella', 'Plaza del Ayuntamiento', '46001', id FROM cities WHERE name = 'Valencia'
AND NOT EXISTS (SELECT 1 FROM neighborhoods WHERE name = 'Ciutat Vella');

INSERT INTO neighborhoods (name, street_name, postal_code, city_id)
SELECT 'Ruzafa', 'Calle de Cádiz', '46006', id FROM cities WHERE name = 'Valencia'
AND NOT EXISTS (SELECT 1 FROM neighborhoods WHERE name = 'Ruzafa');

INSERT INTO neighborhoods (name, street_name, postal_code, city_id)
SELECT 'Benimaclet', 'Calle del Barón de San Petrillo', '46020', id FROM cities WHERE name = 'Valencia'
AND NOT EXISTS (SELECT 1 FROM neighborhoods WHERE name = 'Benimaclet');

INSERT INTO neighborhoods (name, street_name, postal_code, city_id)
SELECT 'El Carmen', 'Calle de la Corona', '46003', id FROM cities WHERE name = 'Valencia'
AND NOT EXISTS (SELECT 1 FROM neighborhoods WHERE name = 'El Carmen');

INSERT INTO neighborhoods (name, street_name, postal_code, city_id)
SELECT 'Patraix', 'Plaza de Patraix', '46017', id FROM cities WHERE name = 'Valencia'
AND NOT EXISTS (SELECT 1 FROM neighborhoods WHERE name = 'Patraix');

INSERT INTO neighborhoods (name, street_name, postal_code, city_id)
SELECT 'Cabanyal', 'Calle de la Reina', '46011', id FROM cities WHERE name = 'Valencia'
AND NOT EXISTS (SELECT 1 FROM neighborhoods WHERE name = 'Cabanyal');

INSERT INTO neighborhoods (name, street_name, postal_code, city_id)
SELECT 'Campanar', 'Avenida de las Jacarandas', '46015', id FROM cities WHERE name = 'Valencia'
AND NOT EXISTS (SELECT 1 FROM neighborhoods WHERE name = 'Campanar');