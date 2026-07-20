-- V21__add_base_entity_fields_to_queues.sql
-- Add missing fields required by BaseEntity

ALTER TABLE email_queue
ADD COLUMN created_by VARCHAR(255),
ADD COLUMN updated_by VARCHAR(255),
ADD COLUMN deleted BOOLEAN NOT NULL DEFAULT FALSE,
ADD COLUMN deleted_at TIMESTAMP,
ADD COLUMN deleted_by VARCHAR(255);

ALTER TABLE whatsapp_queue
ADD COLUMN created_by VARCHAR(255),
ADD COLUMN updated_by VARCHAR(255),
ADD COLUMN deleted BOOLEAN NOT NULL DEFAULT FALSE,
ADD COLUMN deleted_at TIMESTAMP,
ADD COLUMN deleted_by VARCHAR(255);
