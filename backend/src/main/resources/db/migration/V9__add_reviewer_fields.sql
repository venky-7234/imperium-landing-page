-- V9__add_reviewer_fields.sql
-- Add reviewer_id and reviewed_at fields to applications table

ALTER TABLE applications ADD COLUMN reviewer_id BIGINT;
ALTER TABLE applications ADD COLUMN reviewed_at DATETIME(6);

ALTER TABLE applications ADD CONSTRAINT fk_applications_reviewer FOREIGN KEY (reviewer_id) REFERENCES users (id) ON DELETE SET NULL;
