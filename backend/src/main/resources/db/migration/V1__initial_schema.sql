-- ============================================================
--  V1__initial_schema.sql
--  Imperium IMS — Initial database schema
--  Managed by Flyway (classpath:db/migration)
-- ============================================================

-- Roles
CREATE TABLE IF NOT EXISTS roles (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(50)  NOT NULL,
    description VARCHAR(255),
    is_system_role BOOLEAN  NOT NULL DEFAULT FALSE,
    deleted     BOOLEAN      NOT NULL DEFAULT FALSE,
    version     BIGINT,
    created_at  DATETIME(6)  NOT NULL,
    updated_at  DATETIME(6)  NOT NULL,
    created_by  VARCHAR(100),
    updated_by  VARCHAR(100),
    CONSTRAINT uk_roles_name UNIQUE (name)
);

-- Users
CREATE TABLE IF NOT EXISTS users (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    username       VARCHAR(50)  NOT NULL,
    email          VARCHAR(150) NOT NULL,
    password       VARCHAR(255) NOT NULL,
    first_name     VARCHAR(100),
    last_name      VARCHAR(100),
    phone_number   VARCHAR(20),
    avatar_url     VARCHAR(500),
    status         VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    enabled        BOOLEAN      NOT NULL DEFAULT TRUE,
    email_verified BOOLEAN      NOT NULL DEFAULT FALSE,
    last_login_at  DATETIME(6),
    deleted        BOOLEAN      NOT NULL DEFAULT FALSE,
    version        BIGINT,
    created_at     DATETIME(6)  NOT NULL,
    updated_at     DATETIME(6)  NOT NULL,
    created_by     VARCHAR(100),
    updated_by     VARCHAR(100),
    CONSTRAINT uk_users_username UNIQUE (username),
    CONSTRAINT uk_users_email    UNIQUE (email)
);

-- User-Role join table
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles (id)
);

-- Events
CREATE TABLE IF NOT EXISTS events (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    title           VARCHAR(200) NOT NULL,
    description     TEXT,
    venue           VARCHAR(300),
    venue_address   VARCHAR(500),
    city            VARCHAR(100),
    country         VARCHAR(100),
    start_date_time DATETIME(6)  NOT NULL,
    end_date_time   DATETIME(6),
    max_guests      INT,
    rsvp_deadline   DATETIME(6),
    status          VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    event_type      VARCHAR(50),
    cover_image_url VARCHAR(500),
    is_public       BOOLEAN      NOT NULL DEFAULT FALSE,
    organizer_id    BIGINT       NOT NULL,
    deleted         BOOLEAN      NOT NULL DEFAULT FALSE,
    version         BIGINT,
    created_at      DATETIME(6)  NOT NULL,
    updated_at      DATETIME(6)  NOT NULL,
    created_by      VARCHAR(100),
    updated_by      VARCHAR(100),
    CONSTRAINT fk_events_organizer FOREIGN KEY (organizer_id) REFERENCES users (id)
);

-- Invitations
CREATE TABLE IF NOT EXISTS invitations (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_id          BIGINT       NOT NULL,
    guest_name        VARCHAR(200) NOT NULL,
    guest_email       VARCHAR(150),
    guest_phone       VARCHAR(20),
    guest_note        VARCHAR(500),
    status            VARCHAR(30)  NOT NULL DEFAULT 'DRAFT',
    channel           VARCHAR(20)  NOT NULL DEFAULT 'EMAIL',
    token             VARCHAR(100) NOT NULL,
    sent_at           DATETIME(6),
    viewed_at         DATETIME(6),
    responded_at      DATETIME(6),
    expires_at        DATETIME(6),
    response_note     VARCHAR(500),
    additional_guests INT,
    delivery_id       VARCHAR(100),
    delivery_failed   BOOLEAN      NOT NULL DEFAULT FALSE,
    delivery_error    VARCHAR(500),
    deleted           BOOLEAN      NOT NULL DEFAULT FALSE,
    version           BIGINT,
    created_at        DATETIME(6)  NOT NULL,
    updated_at        DATETIME(6)  NOT NULL,
    created_by        VARCHAR(100),
    updated_by        VARCHAR(100),
    CONSTRAINT uk_invitations_token UNIQUE (token),
    CONSTRAINT fk_invitations_event FOREIGN KEY (event_id) REFERENCES events (id),
    INDEX idx_invitation_event  (event_id),
    INDEX idx_invitation_email  (guest_email),
    INDEX idx_invitation_status (status)
);

-- Applications
CREATE TABLE IF NOT EXISTS applications (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_id      BIGINT       NOT NULL,
    first_name    VARCHAR(100) NOT NULL,
    last_name     VARCHAR(100) NOT NULL,
    email         VARCHAR(150) NOT NULL,
    phone         VARCHAR(20),
    social_profile_url VARCHAR(500),
    notes         VARCHAR(1000),
    status        VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    deleted       BOOLEAN      NOT NULL DEFAULT FALSE,
    version       BIGINT,
    created_at    DATETIME(6)  NOT NULL,
    updated_at    DATETIME(6)  NOT NULL,
    created_by    VARCHAR(100),
    updated_by    VARCHAR(100),
    CONSTRAINT fk_applications_event FOREIGN KEY (event_id) REFERENCES events (id)
);

-- Notifications
CREATE TABLE IF NOT EXISTS notifications (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    recipient_id      VARCHAR(150),
    recipient_type    VARCHAR(30),
    title             VARCHAR(200) NOT NULL,
    message           TEXT         NOT NULL,
    channel           VARCHAR(20)  NOT NULL,
    is_read           BOOLEAN      NOT NULL DEFAULT FALSE,
    reference_id      VARCHAR(100),
    reference_type    VARCHAR(50),
    notification_type VARCHAR(50),
    deleted           BOOLEAN      NOT NULL DEFAULT FALSE,
    version           BIGINT,
    created_at        DATETIME(6)  NOT NULL,
    updated_at        DATETIME(6)  NOT NULL,
    created_by        VARCHAR(100),
    updated_by        VARCHAR(100)
);

-- Email Logs
CREATE TABLE IF NOT EXISTS email_logs (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    to_address     VARCHAR(150) NOT NULL,
    subject        VARCHAR(300) NOT NULL,
    template_name  VARCHAR(100),
    status         VARCHAR(20)  NOT NULL,
    error_message  VARCHAR(500),
    sent_at        DATETIME(6),
    reference_id   VARCHAR(100),
    reference_type VARCHAR(50),
    deleted        BOOLEAN      NOT NULL DEFAULT FALSE,
    version        BIGINT,
    created_at     DATETIME(6)  NOT NULL,
    updated_at     DATETIME(6)  NOT NULL,
    created_by     VARCHAR(100),
    updated_by     VARCHAR(100)
);

-- WhatsApp Logs
CREATE TABLE IF NOT EXISTS whatsapp_logs (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    to_number           VARCHAR(20)  NOT NULL,
    message_body        TEXT,
    status              VARCHAR(20)  NOT NULL,
    provider_message_id VARCHAR(100),
    error_message       VARCHAR(500),
    sent_at             DATETIME(6),
    reference_id        VARCHAR(100),
    reference_type      VARCHAR(50),
    deleted             BOOLEAN      NOT NULL DEFAULT FALSE,
    version             BIGINT,
    created_at          DATETIME(6)  NOT NULL,
    updated_at          DATETIME(6)  NOT NULL,
    created_by          VARCHAR(100),
    updated_by          VARCHAR(100)
);

-- Audit Logs
CREATE TABLE IF NOT EXISTS audit_logs (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    timestamp     DATETIME(6)  NOT NULL,
    actor         VARCHAR(100) NOT NULL,
    action        VARCHAR(100) NOT NULL,
    resource_type VARCHAR(50),
    resource_id   VARCHAR(100),
    description   VARCHAR(500),
    ip_address    VARCHAR(45),
    user_agent    VARCHAR(300),
    result        VARCHAR(20),
    INDEX idx_audit_actor    (actor),
    INDEX idx_audit_action   (action),
    INDEX idx_audit_resource (resource_type, resource_id)
);

-- ── Seed Data ───────────────────────────────────────────────────────────────
INSERT IGNORE INTO roles (name, description, is_system_role, deleted, created_at, updated_at)
VALUES
    ('ADMIN',   'System administrator with full access', TRUE,  FALSE, NOW(), NOW()),
    ('MANAGER', 'Event manager',                         TRUE,  FALSE, NOW(), NOW()),
    ('STAFF',   'Staff member',                          FALSE, FALSE, NOW(), NOW()),
    ('GUEST',   'Read-only guest user',                  FALSE, FALSE, NOW(), NOW());
