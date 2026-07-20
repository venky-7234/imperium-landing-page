-- ============================================================
--  V3__complete_schema_refactor.sql
--  Imperium IMS — Align schema with JPA Entities Refactor
-- ============================================================

-- 1. Add public_id to all existing tables (UUID)
ALTER TABLE roles ADD COLUMN public_id BINARY(16) UNIQUE;
ALTER TABLE users ADD COLUMN public_id BINARY(16) UNIQUE;
ALTER TABLE events ADD COLUMN public_id BINARY(16) UNIQUE;
ALTER TABLE invitations ADD COLUMN public_id BINARY(16) UNIQUE;
ALTER TABLE applications ADD COLUMN public_id BINARY(16) UNIQUE;
ALTER TABLE notifications ADD COLUMN public_id BINARY(16) UNIQUE;
ALTER TABLE email_logs ADD COLUMN public_id BINARY(16) UNIQUE;
ALTER TABLE whatsapp_logs ADD COLUMN public_id BINARY(16) UNIQUE;
ALTER TABLE refresh_tokens ADD COLUMN public_id BINARY(16) UNIQUE;
ALTER TABLE password_reset_tokens ADD COLUMN public_id BINARY(16) UNIQUE;
ALTER TABLE audit_logs ADD COLUMN public_id BINARY(16) UNIQUE;

-- 2. Add status fields where missing or rename
ALTER TABLE roles ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE';
ALTER TABLE notifications ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE';

-- Audit Logs refactor (convert from basic table to BaseEntity)
ALTER TABLE audit_logs RENAME COLUMN result TO status;
ALTER TABLE audit_logs RENAME COLUMN timestamp TO created_at;
ALTER TABLE audit_logs RENAME COLUMN actor TO created_by;
ALTER TABLE audit_logs ADD COLUMN updated_at DATETIME(6);
ALTER TABLE audit_logs ADD COLUMN updated_by VARCHAR(100);
ALTER TABLE audit_logs ADD COLUMN deleted BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE audit_logs ADD COLUMN version BIGINT;

-- Drop old indexes and recreate them with the new column names for audit_logs
ALTER TABLE audit_logs DROP INDEX idx_audit_actor;
CREATE INDEX idx_audit_created_by ON audit_logs (created_by);

-- 3. Applications Foreign Key Update
-- (Removed owner constraint, as the table was refactored to guest applications)

-- 4. Create New Tables
-- Application Reviews
CREATE TABLE IF NOT EXISTS application_reviews (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    public_id      BINARY(16) UNIQUE,
    application_id BIGINT       NOT NULL,
    reviewer_id    BIGINT       NOT NULL,
    rating         INT          NOT NULL,
    comments       VARCHAR(1000),
    status         VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    deleted        BOOLEAN      NOT NULL DEFAULT FALSE,
    version        BIGINT,
    created_at     DATETIME(6)  NOT NULL,
    updated_at     DATETIME(6)  NOT NULL,
    created_by     VARCHAR(100),
    updated_by     VARCHAR(100),
    CONSTRAINT fk_application_reviews_app FOREIGN KEY (application_id) REFERENCES applications (id) ON DELETE CASCADE,
    CONSTRAINT fk_application_reviews_user FOREIGN KEY (reviewer_id) REFERENCES users (id) ON DELETE CASCADE
);

-- Settings
CREATE TABLE IF NOT EXISTS settings (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    public_id     BINARY(16) UNIQUE,
    setting_key   VARCHAR(100) NOT NULL,
    setting_value VARCHAR(1000) NOT NULL,
    description   VARCHAR(255),
    status        VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    deleted       BOOLEAN      NOT NULL DEFAULT FALSE,
    version       BIGINT,
    created_at    DATETIME(6)  NOT NULL,
    updated_at    DATETIME(6)  NOT NULL,
    created_by    VARCHAR(100),
    updated_by    VARCHAR(100),
    CONSTRAINT uk_settings_key UNIQUE (setting_key)
);
