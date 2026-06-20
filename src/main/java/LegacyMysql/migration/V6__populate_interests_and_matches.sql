-- =====================================================================
-- 1. ARREGLO ESTRUCTURAL (LO QUE ERA LA V18)
-- =====================================================================


ALTER TABLE matches DROP CHECK chk_different_children;


ALTER TABLE matches
    CHANGE COLUMN child_a_id child_request_id BIGINT NOT NULL,
    CHANGE COLUMN child_b_id child_target_id BIGINT NOT NULL;


ALTER TABLE matches
    ADD CONSTRAINT chk_different_children CHECK (child_request_id <> child_target_id);


-- =====================================================================
-- 2. CARGA DE LOS 16 INTERESES (Limpios y con tipos correctos)
-- =====================================================================

INSERT INTO `interests` (`name`, `type`, `created_at`, `updated_at`) VALUES
('Sensory Play', 'OTHER', NOW(), NOW()),
('Lego & Building', 'TECHNOLOGY', NOW(), NOW()),
('Dinosaurs', 'SCIENCE', NOW(), NOW()),
('Drawing & Painting', 'ARTS', NOW(), NOW()),
('Swimming', 'SPORTS', NOW(), NOW()),
('Music & Dance', 'MUSIC', NOW(), NOW()),
('Storytelling', 'ARTS', NOW(), NOW()),
('Outdoor Exploration', 'OUTDOOR', NOW(), NOW()),
('Animals & Nature', 'OUTDOOR', NOW(), NOW()),
('Board Games', 'OTHER', NOW(), NOW()),
('Cooking for Kids', 'SCIENCE', NOW(), NOW()),
('Space & Planets', 'SCIENCE', NOW(), NOW()),
('Puzzles', 'OTHER', NOW(), NOW()),
('Gardening', 'OUTDOOR', NOW(), NOW()),
('Valencian Pilota', 'SPORTS', NOW(), NOW()),
('Fallas Artist', 'ARTS', NOW(), NOW());


-- =====================================================================
-- 3. VINCULACIÓN DE INTERESES (Usando JOINs compatibles)
-- =====================================================================

-- Intereses para el hijo de Lucia
INSERT INTO `child_interests` (`child_id`, `interest_id`)
SELECT c.id, i.id
FROM `children` c
JOIN `families` f ON c.family_id = f.id
JOIN `users` u ON f.user_id = u.id
JOIN `interests` i ON i.name IN ('Drawing & Painting', 'Storytelling')
WHERE u.email = 'lucia@example.com';

-- Intereses para el hijo de Pedro
INSERT INTO `child_interests` (`child_id`, `interest_id`)
SELECT c.id, i.id
FROM `children` c
JOIN `families` f ON c.family_id = f.id
JOIN `users` u ON f.user_id = u.id
JOIN `interests` i ON i.name IN ('Lego & Building', 'Swimming')
WHERE u.email = 'pedro@example.com';


-- =====================================================================
-- 4. MATCH DE PRUEBA (Utilizando la nueva estructura)
-- =====================================================================

INSERT INTO `matches` (
    `child_request_id`,
    `child_target_id`,
    `status`,
    `created_at`,
    `updated_at`
)
SELECT
    c1.id,
    c2.id,
    'PENDING',
    NOW(),
    NOW()
FROM `children` c1
JOIN `families` f1 ON c1.family_id = f1.id
JOIN `users` u1 ON f1.user_id = u1.id
JOIN `children` c2 ON c1.id != c2.id
JOIN `families` f2 ON c2.family_id = f2.id
JOIN `users` u2 ON f2.user_id = u2.id
WHERE u1.email = 'pedro@example.com'
AND u2.email = 'marcos.ruiz@example.com'
LIMIT 1;