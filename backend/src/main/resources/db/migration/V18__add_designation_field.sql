-- V18__add_designation_field.sql
-- Add designation field to applications table

ALTER TABLE applications
ADD COLUMN designation VARCHAR(150) NULL AFTER company;
