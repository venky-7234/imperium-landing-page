ALTER TABLE applications ADD COLUMN assigned_admin_id BIGINT;
ALTER TABLE applications ADD CONSTRAINT fk_app_assigned_admin FOREIGN KEY (assigned_admin_id) REFERENCES users(id);
