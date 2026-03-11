-- 1. Permitir que birth_date y gender sean NULL para embarazos
ALTER TABLE children ALTER COLUMN birth_date DATE NULL;
ALTER TABLE children ALTER COLUMN gender VARCHAR(255) NULL;

-- 2. Añadir campos para el seguimiento del embarazo
ALTER TABLE children ADD COLUMN due_date DATE NULL;
ALTER TABLE children ADD COLUMN is_prenatal BOOLEAN DEFAULT FALSE NOT NULL;