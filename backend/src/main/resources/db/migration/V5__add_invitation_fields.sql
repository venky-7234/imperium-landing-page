ALTER TABLE invitations
ADD COLUMN invitation_number VARCHAR(50) NULL AFTER status,
ADD COLUMN company VARCHAR(150) NULL AFTER guest_note,
ADD COLUMN designation VARCHAR(150) NULL AFTER company;

CREATE UNIQUE INDEX idx_invitation_number ON invitations(invitation_number);
