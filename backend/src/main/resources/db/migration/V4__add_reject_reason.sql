ALTER TABLE applications 
ADD COLUMN reject_reason VARCHAR(500) NULL AFTER status;
