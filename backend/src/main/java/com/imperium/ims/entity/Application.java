package com.imperium.ims.entity;

import com.imperium.ims.applications.enums.ApplicationStatus;
import com.imperium.ims.entity.BaseEntity;
import com.imperium.ims.entity.Event;
import jakarta.persistence.*;
import lombok.*;

/**
 * Represents a guest application to attend a specific event.
 */
@Entity
@Table(name = "applications", indexes = {
        @Index(name = "idx_application_status", columnList = "status"),
        @Index(name = "idx_application_event_id", columnList = "event_id"),
        @Index(name = "idx_application_email", columnList = "email"),
        @Index(name = "idx_application_public_id", columnList = "public_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Application extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "email", nullable = false, length = 150)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "social_profile_url", length = 500)
    private String socialProfileUrl;

    @Column(name = "notes", length = 1000)
    private String notes;

    @Column(name = "company", length = 150)
    private String company;

    @Column(name = "designation", length = 150)
    private String designation;

    @Column(name = "industry", length = 150)
    private String industry;

    @Column(name = "annual_revenue", length = 100)
    private String annualRevenue;

    @Column(name = "years_in_business", length = 50)
    private String yearsInBusiness;

    @Column(name = "why_attend", columnDefinition = "TEXT")
    private String whyAttend;

    @Column(name = "what_value", columnDefinition = "TEXT")
    private String whatValue;

    @Column(name = "referred_by", length = 150)
    private String referredBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private ApplicationStatus status = ApplicationStatus.PENDING;

    @Column(name = "invitation_number", length = 50)
    private String invitationNumber;

    @Column(name = "reject_reason", length = 500)
    private String rejectReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id")
    private com.imperium.ims.entity.User reviewer;

    @Column(name = "reviewed_at")
    private java.time.LocalDateTime reviewedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_admin_id")
    private com.imperium.ims.entity.User assignedAdmin;
}
