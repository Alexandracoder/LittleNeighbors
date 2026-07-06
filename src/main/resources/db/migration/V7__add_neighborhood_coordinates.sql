-- El mapa de ExplorePage nunca mostraba pines porque family.latitude/longitude
-- no se rellenan en ningún punto del flujo de alta (y no deberían: no pedimos
-- la dirección exacta de una familia con menores, por privacidad). En su
-- lugar, aproximamos la posición al centroide del barrio.
--
-- Coordenadas aproximadas (WGS84) de cada barrio de Valencia. Son
-- aproximaciones razonables para efectos de visualización, no límites
-- catastrales exactos; para mayor precisión en el futuro, el dataset oficial
-- "Barris / Barrios" de Open Data Valencia (valencia.opendatasoft.com)
-- contiene los polígonos exactos.

ALTER TABLE neighborhoods
    ADD COLUMN IF NOT EXISTS latitude  DOUBLE PRECISION,
    ADD COLUMN IF NOT EXISTS longitude DOUBLE PRECISION;

UPDATE neighborhoods SET latitude = 39.4761, longitude = -0.4083 WHERE name = 'Mislata';
UPDATE neighborhoods SET latitude = 39.4746, longitude = -0.3763 WHERE name = 'Ciutat Vella';
UPDATE neighborhoods SET latitude = 39.4610, longitude = -0.3760 WHERE name = 'Ruzafa';
UPDATE neighborhoods SET latitude = 39.4870, longitude = -0.3610 WHERE name = 'Benimaclet';
UPDATE neighborhoods SET latitude = 39.4790, longitude = -0.3790 WHERE name = 'El Carmen';
UPDATE neighborhoods SET latitude = 39.4580, longitude = -0.3880 WHERE name = 'Patraix';
UPDATE neighborhoods SET latitude = 39.4680, longitude = -0.3280 WHERE name = 'Cabanyal';
UPDATE neighborhoods SET latitude = 39.4830, longitude = -0.3950 WHERE name = 'Campanar';
UPDATE neighborhoods SET latitude = 39.4670, longitude = -0.3730 WHERE name = 'Eixample';
UPDATE neighborhoods SET latitude = 39.4730, longitude = -0.3860 WHERE name = 'Extramurs';
UPDATE neighborhoods SET latitude = 39.4620, longitude = -0.4000 WHERE name = 'L Olivereta';
UPDATE neighborhoods SET latitude = 39.4840, longitude = -0.3720 WHERE name = 'La Saidia';
UPDATE neighborhoods SET latitude = 39.4780, longitude = -0.3610 WHERE name = 'El Pla del Real';
UPDATE neighborhoods SET latitude = 39.4760, longitude = -0.3470 WHERE name = 'Algiros';
UPDATE neighborhoods SET latitude = 39.4960, longitude = -0.3870 WHERE name = 'Benicalap';
UPDATE neighborhoods SET latitude = 39.4520, longitude = -0.3650 WHERE name = 'Quatre Carreres';

-- Fallback: cualquier barrio que se añada más adelante sin coordenadas
-- todavía, que al menos caiga en el centro de Valencia y no en (0,0).
UPDATE neighborhoods SET latitude = 39.4699, longitude = -0.3763
WHERE latitude IS NULL OR longitude IS NULL;
