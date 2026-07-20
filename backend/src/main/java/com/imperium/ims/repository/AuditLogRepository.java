package com.imperium.ims.repository;

import com.imperium.ims.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for {@link AuditLog} persistence.
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    Page<AuditLog> findByCreatedBy(String createdBy, Pageable pageable);

    Page<AuditLog> findByResourceTypeAndResourceId(
            String resourceType, String resourceId, Pageable pageable);

    Page<AuditLog> findByAction(String action, Pageable pageable);

    @org.springframework.data.jpa.repository.Query(
        "SELECT a FROM AuditLog a WHERE a.createdBy IN " +
        "(SELECT u.username FROM User u JOIN u.roles r WHERE r.name = 'ADMIN')"
    )
    Page<AuditLog> findAdminActivities(Pageable pageable);

    @org.springframework.data.jpa.repository.Query(
        "SELECT a FROM AuditLog a WHERE a.createdBy = :identifier OR (a.resourceType IN :types AND a.resourceId = :identifier)"
    )
    java.util.List<AuditLog> findByActorOrResourceId(
        @org.springframework.data.repository.query.Param("identifier") String identifier,
        @org.springframework.data.repository.query.Param("types") java.util.List<String> types
    );
}
