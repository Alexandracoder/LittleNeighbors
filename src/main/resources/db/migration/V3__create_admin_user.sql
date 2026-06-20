INSERT INTO users (email, first_name, last_name, password, verification_status)
SELECT 'admin@littleneighbors.com', 'Admin', 'User',
       '$2a$10$xNXEirEe554CFqtTwOcRHuM59sNOIH9yx1y3nB5Rcx5yAwfvPW5ni',
       'VERIFIED'
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE email = 'admin@littleneighbors.com'
);

INSERT INTO user_roles (user_id, role)
SELECT u.id, 'ADMIN'
FROM users u
WHERE u.email = 'admin@littleneighbors.com'
AND NOT EXISTS (
    SELECT 1 FROM user_roles ur WHERE ur.user_id = u.id AND ur.role = 'ADMIN'
);