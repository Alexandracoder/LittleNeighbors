-- V3__insert_initial_roles.sql
-- Insert initial roles and admin user

-- 1. Insert an initial admin user (si aún no existe)
INSERT INTO users (email, first_name, last_name, password, created_at, updated_at)
SELECT 'admin@littleneighbors.com', 'Admin', 'User', '$2a$10$hashedpasswordhere', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE email = 'admin@littleneighbors.com'
);

-- 2. Assign ADMIN role to the admin user
INSERT INTO user_roles (user_id, role)
SELECT u.id, 'ADMIN'
FROM users u
WHERE u.email = 'admin@littleneighbors.com'
  AND NOT EXISTS (
      SELECT 1 FROM user_roles ur WHERE ur.user_id = u.id AND ur.role = 'ADMIN'
);

-- 3. Assign FAMILY role to all other users
INSERT INTO user_roles (user_id, role)
SELECT u.id, 'FAMILY'
FROM users u
WHERE u.email != 'admin@littleneighbors.com'
  AND NOT EXISTS (
      SELECT 1 FROM user_roles ur WHERE ur.user_id = u.id AND ur.role = 'FAMILY'
);
