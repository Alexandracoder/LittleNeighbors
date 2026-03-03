-- 1. INSERT INTERESTS
INSERT INTO `interests` (`name`, `type`) VALUES
('Football', 'SPORTS'),
('Basketball', 'SPORTS'),
('Painting', 'ARTS'),
('Piano', 'MUSIC'),
('Robotics', 'SCIENCE'),
('Chess', 'TECHNOLOGY'),
('Hiking', 'OUTDOOR'),
('Theater', 'OTHER');

-- 2. LINK INTERESTS
-- Niño de Lucía
INSERT INTO `child_interests` (`child_id`, `interest_id`)
SELECT c.id, i.id
FROM `children` c
JOIN `families` f ON c.family_id = f.id
JOIN `users` u ON f.user_id = u.id
CROSS JOIN `interests` i
WHERE u.email = 'lucia@example.com' AND i.name IN ('Painting', 'Theater');

-- Niño de Pedro
INSERT INTO `child_interests` (`child_id`, `interest_id`)
SELECT c.id, i.id
FROM `children` c
JOIN `families` f ON c.family_id = f.id
JOIN `users` u ON f.user_id = u.id
CROSS JOIN `interests` i
WHERE u.email = 'pedro@example.com' AND i.name IN ('Football', 'Robotics');

-- 3. INSERT TEST MATCH
INSERT INTO `matches` (`child_a_id`, `child_b_id`, `status`)
SELECT c1.id, c2.id, 'PENDING'
FROM `children` c1
JOIN `families` f1 ON c1.family_id = f1.id
JOIN `users` u1 ON f1.user_id = u1.id
JOIN `children` c2 ON c1.id != c2.id
JOIN `families` f2 ON c2.family_id = f2.id
JOIN `users` u2 ON f2.user_id = u2.id
WHERE u1.email = 'pedro@example.com' AND u2.email = 'marcos.ruiz@example.com'
LIMIT 1;