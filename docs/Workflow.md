# System Workflows (Version 1.0)

This document describes the core business logic workflows within the Imperium Invitation Management System.

## 1. Authentication & Onboarding
- **Initial Login**: Users log in using Google OAuth. If the email exists in the database, a JWT is issued. If it does not exist, the login is rejected (no auto-registration).
- **Role Assignment**: `SUPER_ADMIN` assigns roles (`ADMIN`, `USER`) to newly created accounts. Guests do not have accounts; they operate via public application forms.

## 2. Event Lifecycle
1. **Creation**: `ADMIN` or `SUPER_ADMIN` creates an Event (VIP Gala, Corporate Summit).
2. **Configuration**: Limits, dates, and application forms are configured.
3. **Application Phase**:
   - Guests visit the public event page.
   - Guests submit an Application (`POST /api/applications`).
4. **Review Phase**:
   - Internal users review the applications.
   - Upon approval, the Guest is moved to the `INVITATIONS` table.
5. **Invitation Dispatch**:
   - The system generates a secure, tamper-proof PDF with a QR Code.
   - The system triggers the `EmailAutomationService` and `WhatsAppIntegrationService` to dispatch the invitation.
6. **RSVP**: Guests click the secure link in their email to confirm attendance.

## 3. Event Execution (Scanning)
- During the event, security staff use the Dashboard to scan Guest QR codes.
- The system validates the signature and updates the Invitation status to `SCANNED_IN`.

## 4. Analytics & Reporting
- The Dashboard pulls aggregated data from the Analytics Service.
- Tracks metrics like `Total Applications`, `Approval Rate`, `RSVP Rate`, and `Check-in Count`.
