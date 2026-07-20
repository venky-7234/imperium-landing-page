-- Remove all developer and test accounts to prepare for production deployment
-- The system is now ready for a production Super Admin to be inserted manually.

SET FOREIGN_KEY_CHECKS = 0;
DELETE FROM user_roles;
DELETE FROM events;
DELETE FROM users;
SET FOREIGN_KEY_CHECKS = 1;
