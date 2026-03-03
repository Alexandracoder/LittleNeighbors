ALTER TABLE interests ADD COLUMN icon VARCHAR(500);


ALTER TABLE interests CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;


INSERT INTO interests (name, type, icon, created_at, updated_at) VALUES
('Valencian Pilota', 'SPORTS', '⚪🖐️', NOW(), NOW()),
('Local Football (Amunt!)', 'SPORTS', '⚽🦇', NOW(), NOW()),
('Fallas Artist Workshop', 'ARTS', '🎨🔥', NOW(), NOW()),
('Traditional Muixeranga', 'ARTS', '🗼🎺', NOW(), NOW()),
('Wind Band Music', 'MUSIC', '🎺🎷', NOW(), NOW()),
('Albufera Boat Trips', 'OUTDOOR', '🚣🌅', NOW(), NOW()),
('Calderona Mountain Hiking', 'OUTDOOR', '🥾⛰️', NOW(), NOW()),
('Robotics at City of Arts', 'TECHNOLOGY', '🤖🏛️', NOW(), NOW());