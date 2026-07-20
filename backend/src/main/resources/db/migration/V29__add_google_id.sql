-- Add google_id to users table for Google OAuth mapping
ALTER TABLE users ADD COLUMN google_id VARCHAR(255) DEFAULT NULL;
CREATE UNIQUE INDEX uk_users_google_id ON users(google_id);
