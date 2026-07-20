-- ============================================================
--  V2__auth_schema.sql
--  Imperium IMS — Auth schema updates (Tokens, Super Admin)
-- ============================================================

-- Add SUPER_ADMIN role if not exists
INSERT IGNORE INTO roles (name, description, is_system_role, deleted, created_at, updated_at)
VALUES ('SUPER_ADMIN', 'Master administrator with full system control', TRUE, FALSE, NOW(), NOW());

-- Refresh Tokens
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT       NOT NULL,
    token       VARCHAR(255) NOT NULL,
    expiry_date DATETIME(6)  NOT NULL,
    deleted     BOOLEAN      NOT NULL DEFAULT FALSE,
    version     BIGINT,
    created_at  DATETIME(6)  NOT NULL,
    updated_at  DATETIME(6)  NOT NULL,
    created_by  VARCHAR(100),
    updated_by  VARCHAR(100),
    CONSTRAINT uk_refresh_tokens_token UNIQUE (token),
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- Password Reset Tokens
CREATE TABLE IF NOT EXISTS password_reset_tokens (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT       NOT NULL,
    token       VARCHAR(255) NOT NULL,
    expiry_date DATETIME(6)  NOT NULL,
    used        BOOLEAN      NOT NULL DEFAULT FALSE,
    deleted     BOOLEAN      NOT NULL DEFAULT FALSE,
    version     BIGINT,
    created_at  DATETIME(6)  NOT NULL,
    updated_at  DATETIME(6)  NOT NULL,
    created_by  VARCHAR(100),
    updated_by  VARCHAR(100),
    CONSTRAINT uk_password_reset_tokens_token UNIQUE (token),
    CONSTRAINT fk_password_reset_tokens_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);
