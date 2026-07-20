-- V19__assign_admin_to_event.sql
-- Assign admin@viora.com to the Imperium 2026 event so they can see applications

INSERT IGNORE INTO user_assigned_events (user_id, event_id)
SELECT u.id, e.id 
FROM users u, events e 
WHERE u.email = 'admin@viora.com' 
AND e.title = 'Imperium 2026';
