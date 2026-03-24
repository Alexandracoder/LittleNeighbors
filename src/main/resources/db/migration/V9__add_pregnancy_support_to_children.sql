-- 1. Permitir que birth_date y gender sean NULL
ALTER TABLE children ALTER COLUMN birth_date DROP NOT NULL;
ALTER TABLE children ALTER COLUMN gender DROP NOT NULL;

-- 2. Añadir las nuevas columnas (si no las habías añadido aún)
ALTER TABLE children ADD COLUMN due_date DATE;
ALTER TABLE children ADD COLUMN is_prenatal BOOLEAN DEFAULT FALSE NOT NULL;