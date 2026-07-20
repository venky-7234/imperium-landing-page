-- V6__seed_admin.sql
-- Seed the admin user and role

INSERT INTO roles (name, description, is_system_role, deleted, created_at, updated_at, version) 
SELECT 'ADMIN', 'System administrator', TRUE, FALSE, NOW(), NOW(), 0 
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ADMIN');

INSERT INTO users (username, email, password, first_name, last_name, status, enabled, email_verified, deleted, created_at, updated_at, version) 
VALUES ('dumpalavenkatesh712', 'dumpalavenkatesh712@gmail.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HCGF/0aAHe5s5Z6P7qPkW', 'System', 'Admin', 'ACTIVE', TRUE, TRUE, FALSE, NOW(), NOW(), 0);

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.email = 'dumpalavenkatesh712@gmail.com' AND r.name = 'ADMIN';
