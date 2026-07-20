package com.imperium.ims.dto;

import com.imperium.ims.applications.enums.ApplicationStatus;
import lombok.Data;

@Data
public class ApplicationSearchRequest {
    private String query; // Searches name, email, phone, company, etc.
    private java.util.UUID eventId;
    private ApplicationStatus status;
    private String company;
    private String city;
    private String industry;
    private String dateFrom;
    private String dateTo;
    private Boolean assignedToMe;
}
