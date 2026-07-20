package com.imperium.ims.service;

import com.imperium.ims.entity.AuditLog;
import com.imperium.ims.dto.PageResponse;
import org.springframework.data.domain.Pageable;

/**
 * Service contract for audit log operations.
 */
public interface AuditService {

    /**
     * Records an audit log entry asynchronously.
     *
     * @param actor        the username or "SYSTEM"
     * @param action       the action taken (e.g. INVITATION_SENT)
     * @param resourceType the resource type (e.g. INVITATION)
     * @param resourceId   the resource's ID
     * @param description  a human-readable description
     * @param result       SUCCESS or FAILURE
     */
    void log(String actor, String action, String resourceType,
             String resourceId, String description, String result);

    PageResponse<AuditLog> getLogsByActor(String actor, Pageable pageable);

    PageResponse<AuditLog> getLogsByResource(String resourceType, String resourceId, Pageable pageable);
}
