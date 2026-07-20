package com.imperium.ims.service.impl;

import com.imperium.ims.repository.WhatsAppLogRepository;
import com.imperium.ims.service.WhatsAppService;
import com.imperium.ims.service.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link WhatsAppService}.
 *
 * <p>Provider integration (Twilio / Meta Cloud API) is injected in future phases.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WhatsAppServiceImpl implements WhatsAppService {

    private final WhatsAppLogRepository whatsAppLogRepository;
    private final AuditService auditService;

    @Async("asyncTaskExecutor")
    @Override
    public void sendMessage(String toNumber, String message) {
        // TODO: Call Twilio / Meta Cloud API, persist WhatsAppLog
        log.info("Sending WhatsApp message to {}", toNumber);
    }

    @Async("asyncTaskExecutor")
    @Override
    public void sendInvitationMessage(Long invitationId) {
        log.info("Queuing WhatsApp invitation for invitation ID {}", invitationId);
        com.imperium.ims.entity.WhatsAppLog logRecord = com.imperium.ims.entity.WhatsAppLog.builder()
                .toNumber("+0000000000")
                .messageBody("You are invited to Viora Elite Genesis")
                .status("PENDING")
                .referenceId(invitationId.toString())
                .referenceType("INVITATION")
                .build();
        whatsAppLogRepository.save(logRecord);
        auditService.log("SYSTEM", "WHATSAPP_SENT", "INVITATION", invitationId.toString(), "Queued WhatsApp invitation", "SUCCESS");
    }

    @Async("asyncTaskExecutor")
    @Override
    public void sendCancellationMessage(Long invitationId) {
        // TODO: Load invitation, build cancellation message, call sendMessage
        log.info("Sending WhatsApp cancellation for invitation ID {}", invitationId);
    }

    @Override
    public void handleDeliveryStatusWebhook(String providerMessageId, String newStatus) {
        // TODO: Find WhatsAppLog by providerMessageId, update status
        log.info("WhatsApp delivery status update — message: {}, status: {}",
                providerMessageId, newStatus);
    }
}
