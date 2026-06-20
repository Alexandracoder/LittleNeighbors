-- 1. Aseguramos que las columnas existentes permitan NULL (si es lo que buscabas)
ALTER TABLE children MODIFY COLUMN birth_date DATE NULL;
ALTER TABLE children MODIFY COLUMN gender VARCHAR(50) NULL;

-- 2. Añadimos las nuevas columnas de una forma más robusta
ALTER TABLE children
    ADD COLUMN is_pregnancy_support TINYINT(1) DEFAULT 0 NOT NULL,
    ADD COLUMN due_date DATE NULL,
    ADD COLUMN is_prenatal BOOLEAN DEFAULT FALSE NOT NULL;