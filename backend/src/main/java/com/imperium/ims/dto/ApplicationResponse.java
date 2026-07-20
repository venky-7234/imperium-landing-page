package com.imperium.ims.dto;

import com.imperium.ims.applications.enums.ApplicationStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ApplicationResponse {
    private UUID publicId;
    private UUID eventId;
    private String eventTitle;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String socialProfileUrl;
    private String notes;
    private String city;
    private String company;
    private String industry;
    private String annualRevenue;
    private String yearsInBusiness;
    private String whyAttend;
    private String whatValue;
    private String rejectReason;
    private String assignedAdminId;
    private String assignedAdminName;
    private String referredBy;
    private ApplicationStatus status;
    private String invitationNumber;
    private LocalDateTime createdAt;
}
