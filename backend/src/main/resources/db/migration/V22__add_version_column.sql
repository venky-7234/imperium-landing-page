-- V22__add_version_column.sql
-- Add missing version column required by BaseEntity

ALTER TABLE email_queue
ADD COLUMN version BIGINT NOT NULL DEFAULT 0;

ALTER TABLE whatsapp_queue
ADD COLUMN version BIGINT NOT NULL DEFAULT 0;
