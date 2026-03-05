ALTER TABLE interests ADD COLUMN icon VARCHAR(500);


INSERT INTO interests (name, type, icon, created_at, updated_at) VALUES
('Valencian Pilota', 'SPORTS', '⚪🖐️', NOW(), NOW()),
('Local Football (Amunt!)', 'SPORTS', '⚽🦇', NOW(), NOW()),
('Fallas Artist Workshop', 'ARTS', '🎨🔥', NOW(), NOW()),
('Traditional Muixeranga', 'ARTS', '🗼🎺', NOW(), NOW()),
('Wind Band Music', 'MUSIC', '🎺🎷', NOW(), NOW()),
('Albufera Boat Trips', 'OUTDOOR', '🚣🌅', NOW(), NOW()),
('Calderona Mountain Hiking', 'OUTDOOR', '🥾⛰️', NOW(), NOW()),
('Robotics at City of Arts', 'TECHNOLOGY', '🤖🏛️', NOW(), NOW());