-- 1. Añadir la columna de forma estándar (MySQL)
ALTER TABLE children ADD COLUMN nickname VARCHAR(50);


UPDATE children
SET nickname = 'Explorador/a'
WHERE nickname IS NULL;

ALTER TABLE children MODIFY COLUMN nickname VARCHAR(50) NOT NULL;