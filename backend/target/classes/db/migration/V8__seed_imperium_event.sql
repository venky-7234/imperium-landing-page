-- V8__seed_imperium_event.sql
-- Seed the default Imperium event using the admin user as the organizer

INSERT INTO events (title, description, venue, venue_address, city, country, start_date_time, end_date_time, max_guests, rsvp_deadline, status, event_type, is_public, organizer_id, deleted, created_at, updated_at, version)
SELECT 'The Imperium Genesis', 'Exclusive VIP Launch Event', 'The Ritz-Carlton', '123 Luxury Ave', 'New York', 'USA', DATE_ADD(NOW(), INTERVAL 30 DAY), DATE_ADD(NOW(), INTERVAL 31 DAY), 100, DATE_ADD(NOW(), INTERVAL 14 DAY), 'ACTIVE', 'VIP', TRUE, u.id, FALSE, NOW(), NOW(), 0
FROM users u 
WHERE u.email = 'dumpalavenkatesh712@gmail.com'
AND NOT EXISTS (SELECT 1 FROM events WHERE title = 'The Imperium Genesis');
