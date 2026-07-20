package com.imperium.ims.service.impl;

import com.imperium.ims.entity.AuditLog;
import com.imperium.ims.repository.AuditLogRepository;
import com.imperium.ims.service.AuditService;
import com.imperium.ims.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of {@link AuditService}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuditServiceImpl implements AuditService {

    private final AuditLogRepository auditLogRepository;

    @Async("asyncTaskExecutor")
    @Override
    public void log(String actor, String action, String resourceType,
                    String resourceId, String description, String result) {
        AuditLog entry = AuditLog.builder()
                .action(action)
                .resourceType(resourceType)
                .resourceId(resourceId)
                .description(description)
                .status(result)
                .build();
        entry.setCreatedBy(actor);
        auditLogRepository.save(entry);
        log.debug("AUDIT | actor={} action={} resource={}/{} result={}",
                actor, action, resourceType, resourceId, result);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AuditLog> getLogsByActor(String actor, Pageable pageable) {
        return PageResponse.of(auditLogRepository.findByCreatedBy(actor, pageable));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AuditLog> getLogsByResource(
            String resourceType, String resourceId, Pageable pageable) {
        return PageResponse.of(
                auditLogRepository.findByResourceTypeAndResourceId(resourceType, resourceId, pageable));
    }
}
