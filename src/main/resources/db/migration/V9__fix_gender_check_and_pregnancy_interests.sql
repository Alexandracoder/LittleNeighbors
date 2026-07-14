-- 1. CORRECCIÓN DE GÉNERO (Tu lógica original)
ALTER TABLE children DROP CONSTRAINT chk_gender;
ALTER TABLE children
    ADD CONSTRAINT chk_gender CHECK (gender IN ('BOY', 'GIRL', 'PREGNANT', 'SURPRISE'));

-- 2. CORRECCIÓN DE INTERESES (Añadiendo el tipo 'PREGNANCY' al constraint)
-- Primero eliminamos el constraint restrictivo actual
ALTER TABLE interests DROP CONSTRAINT chk_interest_type;

-- Lo recreamos incluyendo PREGNANCY junto con los anteriores
ALTER TABLE interests ADD CONSTRAINT chk_interest_type
CHECK (type::text = ANY (ARRAY['SPORTS', 'ARTS', 'MUSIC', 'SCIENCE', 'TECHNOLOGY', 'OUTDOOR', 'OTHER', 'PREGNANCY']));

-- 3. INSERCIÓN DE NUEVOS INTERESES
INSERT INTO interests (name, type, icon, created_at, updated_at) VALUES
('Pregnancy Yoga', 'PREGNANCY', '🧘‍♀️🤰', NOW(), NOW()),
('Birth Preparation Classes', 'PREGNANCY', '📚🤰', NOW(), NOW()),
('Postpartum Support', 'PREGNANCY', '🤝👶', NOW(), NOW()),
('Breastfeeding Support', 'PREGNANCY', '🍼', NOW(), NOW()),
('Maternity Walks', 'PREGNANCY', '🚶‍♀️🤰', NOW(), NOW()),
('Pregnancy Swimming', 'PREGNANCY', '🏊‍♀️🤰', NOW(), NOW()),
('Baby Shower Planning', 'PREGNANCY', '🎁', NOW(), NOW());
