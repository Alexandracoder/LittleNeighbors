-- V4__create_admin_user.sql
-- Insert initial admin user and assign roles

-- 1. Insert admin user if it does not exist
INSERT INTO users (email, first_name, last_name, password, created_at, updated_at)
SELECT 'admin@littleneighbors.com', 'Admin', 'User', '$2a$10$e0MYzXyjpJS7Pd0RVvHwHeFxCq6fvF1rP/5aQb0vKxOZk/0l6vJXa', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE email = 'admin@littleneighbors.com'
);

-- 2. Assign ADMIN role to the admin user if not already assigned
INSERT INTO user_roles (user_id, role)
SELECT u.id, 'ADMIN'
FROM users u
WHERE u.email = 'admin@littleneighbors.com'
  AND NOT EXISTS (
      SELECT 1 FROM user_roles ur WHERE ur.user_id = u.id AND ur.role = 'ADMIN'
);

-- 3. Assign FAMILY role to all other users if not already assigned
INSERT INTO user_roles (user_id, role)
SELECT u.id, 'FAMILY'
FROM users u
WHERE u.email != 'admin@littleneighbors.com'
  AND NOT EXISTS (
      SELECT 1 FROM user_roles ur WHERE ur.user_id = u.id AND ur.role = 'FAMILY'
);