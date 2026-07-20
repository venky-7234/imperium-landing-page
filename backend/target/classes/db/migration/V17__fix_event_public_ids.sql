-- V9__fix_event_public_ids.sql
-- Ensure all events have a valid public_id
UPDATE events 
SET public_id = UNHEX(REPLACE(UUID(), '-', '')) 
WHERE public_id IS NULL;
