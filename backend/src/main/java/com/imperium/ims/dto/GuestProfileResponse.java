package com.imperium.ims.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class GuestProfileResponse {

    // Personal Information
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String city;

    // Professional Information
    private String company;
    private String designation;
    private String industry;
    private String linkedin;

    // Application Details
    private String annualRevenue;
    private String yearsInBusiness;
    private String whyAttend;
    private String whatValue;
    private String referredBy;
    private String notes;

    // Current Application Context
    private String currentApplicationId;
    private String currentApplicationStatus;
    private String currentEventName;
    private String currentEventId;
    private String invitationStatus;
    private String invitationNumber;

    // History
    private List<ApplicationHistoryDto> applicationHistory;
    private List<InvitationHistoryDto> invitationHistory;
    private List<EventHistoryDto> eventsAttended;
    private List<AuditHistoryDto> auditHistory;
    private List<CommunicationHistoryDto> communicationHistory;
    private List<TimelineItemDto> timeline;
    private List<ApprovalHistoryDto> approvalHistory;
    private List<RejectionHistoryDto> rejectionHistory;

    @Data
    @Builder
    public static class ApplicationHistoryDto {
        private String publicId;
        private String eventName;
        private String status;
        private String rejectReason;
        private LocalDateTime appliedAt;
        private LocalDateTime reviewedAt;
        private String reviewedBy;
    }

    @Data
    @Builder
    public static class InvitationHistoryDto {
        private String invitationNumber;
        private String eventName;
        private String status;
        private LocalDateTime issuedAt;
    }

    @Data
    @Builder
    public static class EventHistoryDto {
        private String eventName;
        private String role;
        private LocalDateTime eventDate;
    }

    @Data
    @Builder
    public static class AuditHistoryDto {
        private String action;
        private String actor;
        private String description;
        private LocalDateTime timestamp;
    }

    @Data
    @Builder
    public static class CommunicationHistoryDto {
        private String type; // "EMAIL" or "WHATSAPP"
        private String status;
        private String subjectOrPreview;
        private LocalDateTime sentAt;
    }

    @Data
    @Builder
    public static class TimelineItemDto {
        private LocalDateTime timestamp;
        private String title;
        private String description;
        private String type;
    }

    @Data
    @Builder
    public static class ApprovalHistoryDto {
        private String eventName;
        private String approvedBy;
        private LocalDateTime approvedAt;
        private String invitationNumber;
    }

    @Data
    @Builder
    public static class RejectionHistoryDto {
        private String eventName;
        private String rejectedBy;
        private LocalDateTime rejectedAt;
        private String reason;
    }
}
