package com.imperium.ims.entity;

import com.imperium.ims.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Records every outbound WhatsApp message for audit and retry.
 */
@Entity
@Table(name = "whatsapp_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WhatsAppLog extends BaseEntity {

    @Column(name = "to_number", nullable = false, length = 20)
    private String toNumber;

    @Column(name = "message_body", columnDefinition = "TEXT")
    private String messageBody;

    @Column(name = "status", nullable = false, length = 20)
    private String status;        // SENT, FAILED, QUEUED, DELIVERED

    @Column(name = "provider_message_id", length = 100)
    private String providerMessageId;

    @Column(name = "error_message", length = 500)
    private String errorMessage;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "reference_id", length = 100)
    private String referenceId;

    @Column(name = "reference_type", length = 50)
    private String referenceType;
}
