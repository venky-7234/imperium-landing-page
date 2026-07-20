# Production Deployment Guide (Version 1.0)

This guide covers the deployment strategy for the Imperium Invitation Management System. The architecture strictly follows a layered design to ensure maintainability, scalability, and security.

## Infrastructure Stack
- **Frontend**: React + Vite + TailwindCSS (Deployed on Vercel, Netlify, or AWS S3+CloudFront)
- **Backend**: Spring Boot 3.3.0 (Deployed on AWS EC2, ECS, or Railway)
- **Database**: MySQL 8+ (AWS RDS or managed DB)
- **Email**: SMTP Server (SendGrid, Amazon SES)
- **Auth**: Google OAuth 2.0 (Identity Provider)

## Pre-Deployment Checklist

### 1. Database Preparation
As part of the V1.0 launch, all development data must be cleared.
1. Run Flyway Migrations in order.
2. The `V32__production_cleanup.sql` will drop all development artifacts (test events, dummy users, fake applications).
3. Insert the CEO's Google Email as the initial Super Admin.
   ```sql
   INSERT INTO users (username, email, first_name, last_name, status, created_at, updated_at) 
   VALUES ('Mahendrareddy.gonu1624@gmail.com', 'Mahendrareddy.gonu1624@gmail.com', 'CEO', 'Admin', 'ACTIVE', NOW(), NOW());
   
   -- Assign SUPER_ADMIN role
   INSERT INTO user_roles (user_id, role_id) 
   SELECT u.id, r.id FROM users u, roles r 
   WHERE u.email = 'Mahendrareddy.gonu1624@gmail.com' AND r.name = 'SUPER_ADMIN';
   ```

### 2. Secrets Management
The following environment variables must be securely injected via a secret manager (AWS Secrets Manager, GitHub Actions Secrets):

**Backend (application-prod.yml)**:
- `DB_URL`: Production MySQL Endpoint
- `DB_USERNAME` / `DB_PASSWORD`
- `JWT_SECRET`: Base64 encoded 256-bit secure key
- `GOOGLE_CLIENT_ID`: OAuth Client ID
- `MAIL_USERNAME` / `MAIL_PASSWORD`

**Frontend (.env.production)**:
- `VITE_API_URL`: The public-facing endpoint of the backend (e.g., `https://api.vioraimperium.com/api`)
- `VITE_GOOGLE_CLIENT_ID`: OAuth Client ID

## CI/CD Pipeline

### Build Process
1. **Frontend**: `npm install && npm run build` (Outputs to `dist/`)
2. **Backend**: `mvn clean package -DskipTests` (Outputs `ims-backend-1.0.0.jar`)

### Deployment Steps
1. Deploy database schema via Flyway (runs automatically on backend startup).
2. Deploy backend service behind a reverse proxy (Nginx/ALB) terminating SSL.
3. Serve frontend static assets via CDN.

## Security Posture
- **No dev/test bootstrap logic**: Removed all auto-provisioning of users.
- **Strict JWT verification**: Tokens are signed using HS256 with 15m expiration.
- **Role-Based Access Control**: Fully enforced on all endpoints.
