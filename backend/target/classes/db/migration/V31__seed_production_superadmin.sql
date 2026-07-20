-- V31__seed_production_superadmin.sql
-- Seed the production superadmin user

INSERT INTO users (username, email, password, first_name, last_name, status, enabled, email_verified, deleted, created_at, updated_at, version) 
VALUES ('MahendraReddy', 'Mahendrareddy.gonu1624@gmail.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HCGF/0aAHe5s5Z6P7qPkW', 'Mahendra', 'Reddy', 'ACTIVE', TRUE, TRUE, FALSE, NOW(), NOW(), 0);

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.email = 'Mahendrareddy.gonu1624@gmail.com' AND r.name = 'SUPER_ADMIN';
