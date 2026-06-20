-- =====================================================================
-- V2__seed_reference_data.sql
-- Datos de referencia/catálogo que la app necesita para funcionar:
--   - Ciudad Valencia + sus 16 barrios (originalmente V5, V12, V13)
--   - Catálogo de intereses (originalmente V6, V8)

INSERT INTO cities (name) VALUES ('Valencia');

-- Códigos postales verificados calle a calle (no por aproximación de barrio).
-- OJO: en Valencia un barrio puede caer en varios códigos postales distintos
-- según la calle; aquí va el que corresponde exactamente a la calle indicada.
-- Mislata es en realidad un municipio independiente de Valencia, no un barrio
-- de la ciudad (se mantiene en esta tabla porque así está modelado en el
-- proyecto, pero ten en cuenta esa diferencia conceptual).
INSERT INTO neighborhoods (name, street_name, postal_code, city_id)
SELECT v.name, v.street_name, v.postal_code, c.id
FROM (VALUES
    ('Mislata', 'Calle Mayor', '46920'),
    ('Ciutat Vella', 'Plaza del Ayuntamiento', '46001'),
    ('Ruzafa', 'Calle de Cádiz', '46006'),
    ('Benimaclet', 'Calle del Barón de San Petrillo', '46020'),
    ('El Carmen', 'Calle de la Corona', '46003'),
    ('Patraix', 'Plaza de Patraix', '46018'),
    ('Cabanyal', 'Calle de la Reina', '46011'),
    ('Campanar', 'Avenida Pío XII', '46015'),
    ('Eixample', 'Gran Vía del Marqués del Turia', '46005'),
    ('Extramurs', 'Gran Vía de Fernando el Católico', '46008'),
    ('L Olivereta', 'Avenida del Cid', '46018'),
    ('La Saidia', 'Calle de Sagunto', '46009'),
    ('El Pla del Real', 'Avenida de Blasco Ibáñez', '46010'),
    ('Algiros', 'Avenida de los Naranjos', '46022'),
    ('Benicalap', 'Avenida de Burjasot', '46025'),
    ('Quatre Carreres', 'Avenida de la Plata', '46006')
) AS v(name, street_name, postal_code)
CROSS JOIN cities c
WHERE c.name = 'Valencia';

INSERT INTO interests (name, type, icon) VALUES
('Sensory Play', 'OTHER', NULL),
('Lego & Building', 'TECHNOLOGY', NULL),
('Dinosaurs', 'SCIENCE', NULL),
('Drawing & Painting', 'ARTS', NULL),
('Swimming', 'SPORTS', NULL),
('Music & Dance', 'MUSIC', NULL),
('Storytelling', 'ARTS', NULL),
('Outdoor Exploration', 'OUTDOOR', NULL),
('Animals & Nature', 'OUTDOOR', NULL),
('Board Games', 'OTHER', NULL),
('Cooking for Kids', 'SCIENCE', NULL),
('Space & Planets', 'SCIENCE', NULL),
('Puzzles', 'OTHER', NULL),
('Gardening', 'OUTDOOR', NULL),
('Valencian Pilota', 'SPORTS', '⚪🖐️'),
('Fallas Artist', 'ARTS', NULL),
('Fallas Artist Workshop', 'ARTS', '🎨🔥'),
('Local Football (Amunt!)', 'SPORTS', '⚽🦇'),
('Traditional Muixeranga', 'ARTS', '🗼🎺'),
('Wind Band Music', 'MUSIC', '🎺🎷'),
('Albufera Boat Trips', 'OUTDOOR', '🚣🌅'),
('Calderona Mountain Hiking', 'OUTDOOR', '🥾⛰️'),
('Robotics at City of Arts', 'TECHNOLOGY', '🤖🏛️');