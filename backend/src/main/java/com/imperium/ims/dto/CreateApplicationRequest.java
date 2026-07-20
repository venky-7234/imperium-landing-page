package com.imperium.ims.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateApplicationRequest {
    
    private Long eventId;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    private String contactNumber;
    private String dob;
    private String city;
    private String company;
    private String industry;
    private String linkedin;
    private String instagram;
    private String annualRevenue;
    private String yearsInBusiness;
    private String whyAttend;
    private String whatValue;
    private String referredBy;
    private Boolean confirmed;
}
