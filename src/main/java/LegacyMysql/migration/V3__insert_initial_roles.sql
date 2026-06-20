INSERT INTO users (email, first_name, last_name, password, verification_status, created_at, updated_at)
SELECT 'admin@littleneighbors.com', 'Admin', 'User', '$2a$10$eO1vR11rQ1/rK6p1.v2jR.wZ5/V6t/z.W4y.Oq2xP3Q0zM6vNqV4q', 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@littleneighbors.com');


INSERT INTO user_roles (user_id, role)
SELECT u.id, 'ADMIN'
FROM users u
WHERE u.email = 'admin@littleneighbors.com'
AND NOT EXISTS (SELECT 1 FROM user_roles ur WHERE ur.user_id = u.id AND ur.role = 'ADMIN');