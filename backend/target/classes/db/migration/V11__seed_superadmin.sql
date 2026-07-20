-- V9__seed_superadmin.sql
-- Seed the superadmin user

INSERT INTO users (username, email, password, first_name, last_name, status, enabled, email_verified, deleted, created_at, updated_at, version) 
VALUES ('superadmin', 'superadmin@vioraimperium.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HCGF/0aAHe5s5Z6P7qPkW', 'Super', 'Admin', 'ACTIVE', TRUE, TRUE, FALSE, NOW(), NOW(), 0);

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.email = 'superadmin@vioraimperium.com' AND r.name = 'SUPER_ADMIN';
