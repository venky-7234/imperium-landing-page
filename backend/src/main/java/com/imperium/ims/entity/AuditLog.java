package com.imperium.ims.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Immutable audit log entry — records every significant system action.
 *
 * <p>Audit entries are NEVER updated or soft-deleted. They grow append-only.
 */
@Entity
@Table(name = "audit_logs", indexes = {
        @Index(name = "idx_audit_actor",    columnList = "created_by"),
        @Index(name = "idx_audit_action",   columnList = "action"),
        @Index(name = "idx_audit_resource", columnList = "resource_type, resource_id")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog extends com.imperium.ims.entity.BaseEntity {

    @Column(name = "action", nullable = false, length = 100)
    private String action;        // e.g. INVITATION_SENT, USER_CREATED, RSVP_SUBMITTED

    @Column(name = "resource_type", length = 50)
    private String resourceType;  // e.g. INVITATION, EVENT, USER

    @Column(name = "resource_id", length = 100)
    private String resourceId;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 300)
    private String userAgent;

    @Column(name = "status", length = 20)
    private String status;        // SUCCESS, FAILURE
}
