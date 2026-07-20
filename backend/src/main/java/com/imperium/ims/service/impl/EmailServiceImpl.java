package com.imperium.ims.service.impl;

import com.imperium.ims.entity.Application;
import com.imperium.ims.dto.EmailRequest;
import com.imperium.ims.entity.EmailLog;
import com.imperium.ims.repository.EmailLogRepository;
import com.imperium.ims.service.EmailService;
import com.imperium.ims.service.AuditService;
import com.imperium.ims.entity.Invitation;
import com.imperium.ims.repository.InvitationRepository;
import com.imperium.ims.invitations.utils.QrCodeGenerator;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final EmailLogRepository emailLogRepository;
    private final InvitationRepository invitationRepository;
    private final AuditService auditService;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Async("asyncTaskExecutor")
    @Override
    public void sendEmail(EmailRequest request) {
        log.info("Sending email to {} — subject: {}", request.getTo(), request.getSubject());
        try {
            sendHtmlEmail(request.getTo(), request.getSubject(), request.getHtmlBody());
            persistLog(request.getTo(), request.getSubject(), request.getTemplateName(), "SENT", null,
                    request.getReferenceId(), request.getReferenceType());
        } catch (Exception ex) {
            log.error("Failed to send email to {}: {}", request.getTo(), ex.getMessage(), ex);
            persistLog(request.getTo(), request.getSubject(), request.getTemplateName(), "FAILED", ex.getMessage(),
                    request.getReferenceId(), request.getReferenceType());
        }
    }

    @Async("asyncTaskExecutor")
    @Override
    public void sendApplicationReceived(Application app) {
        log.info("Sending application received email to {}", app.getEmail());
        String subject = "Application Received – Viora Imperium";
        String templateName = "email/application-received";

        try {
            Context context = new Context();
            context.setVariable("guestName", app.getFirstName());
            context.setVariable("eventName",
                    app.getEvent() != null ? app.getEvent().getTitle() : "Viora Elite Genesis");
            context.setVariable("applicationRef", "APP-" + app.getPublicId().toString().substring(0, 8).toUpperCase());
            context.setVariable("dateSubmitted", app.getCreatedAt() != null
                    ? java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm").format(app.getCreatedAt())
                    : java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm").format(LocalDateTime.now()));
            context.setVariable("supportEmail", "support@vioraimperium.com");

            String htmlContent = templateEngine.process(templateName, context);
            sendHtmlEmail(app.getEmail(), subject, htmlContent);

            persistLog(app.getEmail(), subject, templateName, "SENT", null, app.getId().toString(),
                    "APPLICATION_RECEIVED");
        } catch (Exception e) {
            log.error("Failed to send application received email to {}: {}", app.getEmail(), e.getMessage(), e);
            persistLog(app.getEmail(), subject, templateName, "FAILED", e.getMessage(), app.getId().toString(),
                    "APPLICATION_RECEIVED");
        }
    }

    @Async("asyncTaskExecutor")
    @Override
    public void sendApplicationRejected(Application app) {
        log.info("Sending application rejected email to {}", app.getEmail());
        String subject = "Application Update – Viora Imperium";
        String templateName = "email/application-rejected";

        try {
            Context context = new Context();
            context.setVariable("guestName", app.getFirstName());
            context.setVariable("eventName",
                    app.getEvent() != null ? app.getEvent().getTitle() : "Viora Elite Genesis");

            String htmlContent = templateEngine.process(templateName, context);
            sendHtmlEmail(app.getEmail(), subject, htmlContent);

            persistLog(app.getEmail(), subject, templateName, "SENT", null, app.getId().toString(), "APPLICATION");
        } catch (Exception e) {
            log.error("Failed to send application rejected email to {}: {}", app.getEmail(), e.getMessage(), e);
            persistLog(app.getEmail(), subject, templateName, "FAILED", e.getMessage(), app.getId().toString(),
                    "APPLICATION");
        }
    }

    @Async("asyncTaskExecutor")
    @Override
    public void sendInvitationEmail(Long invitationId) {
        log.info("Sending invitation approved email for invitation ID {}", invitationId);
        String subject = "Congratulations! Your Invitation Has Been Approved";
        String templateName = "email/application-approved";

        try {
            Invitation invitation = invitationRepository.findById(invitationId)
                    .orElseThrow(() -> new IllegalArgumentException("Invitation not found: " + invitationId));

            Context context = new Context();
            context.setVariable("guestName", invitation.getGuestName());
            context.setVariable("eventName", invitation.getEvent().getTitle());
            context.setVariable("eventDate", invitation.getEvent().getStartDateTime().toLocalDate().toString());
            context.setVariable("eventTime", invitation.getEvent().getStartDateTime().toLocalTime().toString());
            context.setVariable("eventVenue", invitation.getEvent().getVenue());
            context.setVariable("invitationNumber", invitation.getInvitationNumber());

            // Generate QR Code data URL
            String qrUrl = "https://vioraimperium.com/verify?token=" + invitation.getToken();
            String qrCodeDataUrl = "data:image/png;base64," + QrCodeGenerator.generateQrCodeBase64(qrUrl, 200, 200);
            context.setVariable("qrCodeDataUrl", qrCodeDataUrl);

            // Download link for the PDF
            context.setVariable("downloadLink", "http://localhost:5173/invitation/download/" + invitation.getToken());

            String htmlContent = templateEngine.process(templateName, context);
            sendHtmlEmail(invitation.getGuestEmail(), subject, htmlContent);

            persistLog(invitation.getGuestEmail(), subject, templateName, "SENT", null, invitationId.toString(),
                    "INVITATION");
        } catch (Exception e) {
            log.error("Failed to send invitation approved email for ID {}: {}", invitationId, e.getMessage(), e);
            persistLog("guest_" + invitationId, subject, templateName, "FAILED", e.getMessage(),
                    invitationId.toString(), "INVITATION");
        }
    }

    @Async("asyncTaskExecutor")
    @Override
    public void sendCancellationNotice(Long eventId) {
        log.info("Sending cancellation notices for event ID {}", eventId);
    }

    @Async("asyncTaskExecutor")
    @Override
    public void sendPasswordResetEmail(String email, String resetToken) {
        log.info("Sending password reset email to {}", email);
    }

    @Async("asyncTaskExecutor")
    @Override
    public void sendVerificationEmail(String email, String verificationToken) {
        log.info("Sending verification email to {}", email);
    }

    // ── Private Helpers ────────────────────────────────────────────────────

    private void sendHtmlEmail(String to, String subject, String htmlBody) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);
        mailSender.send(message);
    }

    private void persistLog(String to, String subject, String templateName, String status, String error, String refId,
            String refType) {
        EmailLog logRec = EmailLog.builder()
                .toAddress(to)
                .subject(subject)
                .templateName(templateName)
                .status(status)
                .errorMessage(error)
                .sentAt("SENT".equals(status) ? LocalDateTime.now() : null)
                .referenceId(refId)
                .referenceType(refType)
                .build();
        emailLogRepository.save(logRec);

        if ("SENT".equals(status)) {
            auditService.log("SYSTEM", "EMAIL_SENT", refType, refId,
                    "Sent email with subject: " + subject + " to " + to, status);
        }
    }
}
