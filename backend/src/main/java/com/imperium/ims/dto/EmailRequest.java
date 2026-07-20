package com.imperium.ims.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * Request object for dispatching an email.
 */
@Data
@Builder
public class EmailRequest {

    private String to;
    private String subject;
    private String templateName;
    private Map<String, Object> templateVariables;
    private String htmlBody;           // Used if no template
    private String referenceId;
    private String referenceType;
}
