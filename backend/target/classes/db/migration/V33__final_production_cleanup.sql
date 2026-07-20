-- Temporarily disable foreign key checks to allow wiping tables cleanly
SET FOREIGN_KEY_CHECKS = 0;

-- 1. Wipe all transactional/dev data
DELETE FROM audit_logs;
DELETE FROM notifications;
DELETE FROM applications;
DELETE FROM invitations;

-- 2. Wipe all users and their role assignments (EXCEPT CEO if they already exist somehow)
DELETE FROM user_roles;
DELETE FROM users WHERE email != 'Mahendrareddy.gonu1624@gmail.com';

-- 3. Ensure the CEO Super Admin account exists
INSERT IGNORE INTO users (username, password, email, first_name, last_name, status, created_at, updated_at)
VALUES (
    'Mahendrareddy.gonu1624@gmail.com', 
    'N/A', -- Relying on Google OAuth, password is not used
    'Mahendrareddy.gonu1624@gmail.com', 
    'CEO', 
    'Admin', 
    'ACTIVE', 
    NOW(), 
    NOW()
);

-- 4. Re-assign SUPER_ADMIN role exclusively to the CEO
-- (Using INSERT IGNORE to prevent duplicates if it was somehow preserved)
INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT u.id, r.id 
FROM users u, roles r 
WHERE u.email = 'Mahendrareddy.gonu1624@gmail.com' AND r.name = 'SUPER_ADMIN';

-- Re-enable foreign key checks
SET FOREIGN_KEY_CHECKS = 1;
