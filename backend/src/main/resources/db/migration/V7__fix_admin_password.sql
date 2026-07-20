-- V7__fix_admin_password.sql
-- Fix the admin password hash

UPDATE users 
SET password = '$2a$12$sfnI4C2p2eISMYwTBg1y9OBfD5d0Ruq9IDu1qLtrEwK/MgLOzvS72' 
WHERE email = 'dumpalavenkatesh712@gmail.com';
