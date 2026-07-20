package com.imperium.ims.dto;

import lombok.Data;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

@Data
public class BulkActionRequest {
    @NotEmpty(message = "Application IDs list cannot be empty")
    private List<UUID> publicIds;
    
    private String reason; // Used for bulk reject
}
