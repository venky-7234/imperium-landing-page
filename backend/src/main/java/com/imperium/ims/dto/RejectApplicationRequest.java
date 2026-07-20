package com.imperium.ims.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RejectApplicationRequest {
    @NotBlank(message = "Reject reason is required")
    @Size(max = 500, message = "Reason cannot exceed 500 characters")
    private String reason;
}
