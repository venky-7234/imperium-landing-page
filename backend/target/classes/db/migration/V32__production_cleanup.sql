-- V32__production_cleanup.sql
-- Final sweep to remove all testing data, applications, notifications, and logs before production use.
-- This ensures the production database starts completely clean.

DELETE FROM notifications;
DELETE FROM audit_logs;
DELETE FROM invitations;
DELETE FROM applications;
