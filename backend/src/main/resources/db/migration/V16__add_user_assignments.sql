-- ============================================================
--  V16__add_user_assignments.sql
--  Imperium IMS — Add user-event and user-guest relationships
-- ============================================================

CREATE TABLE IF NOT EXISTS user_assigned_events (
    user_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, event_id),
    CONSTRAINT fk_user_assigned_events_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_assigned_events_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS user_assigned_guests (
    user_id BIGINT NOT NULL,
    guest_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, guest_id),
    CONSTRAINT fk_user_assigned_guests_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_assigned_guests_guest FOREIGN KEY (guest_id) REFERENCES users(id) ON DELETE CASCADE
);
