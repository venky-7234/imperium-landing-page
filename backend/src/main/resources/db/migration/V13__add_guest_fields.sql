-- V13__add_guest_fields.sql
-- Add city and invitation_number to applications table for advanced guest filtering

ALTER TABLE applications
ADD COLUMN city VARCHAR(100) NULL AFTER phone,
ADD COLUMN invitation_number VARCHAR(50) NULL AFTER status;

-- Add index on invitation_number for faster searches
CREATE INDEX idx_applications_invitation_number ON applications(invitation_number);
