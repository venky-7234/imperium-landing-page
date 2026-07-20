package com.imperium.ims.entity;

import com.imperium.ims.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Persists every outbound email dispatch for audit and retry purposes.
 */
@Entity
@Table(name = "email_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailLog extends BaseEntity {

    @Column(name = "to_address", nullable = false, length = 150)
    private String toAddress;

    @Column(name = "subject", nullable = false, length = 300)
    private String subject;

    @Column(name = "template_name", length = 100)
    private String templateName;

    @Column(name = "status", nullable = false, length = 20)
    private String status;       // SENT, FAILED, QUEUED

    @Column(name = "error_message", length = 500)
    private String errorMessage;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "reference_id", length = 100)
    private String referenceId;  // Invitation / Notification ID

    @Column(name = "reference_type", length = 50)
    private String referenceType;
}
