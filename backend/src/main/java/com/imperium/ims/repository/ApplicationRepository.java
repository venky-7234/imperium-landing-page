package com.imperium.ims.repository;

import com.imperium.ims.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for {@link Application} persistence.
 */
@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long>, JpaSpecificationExecutor<Application> {

    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"event", "assignedAdmin"})
    org.springframework.data.domain.Page<Application> findAll(org.springframework.data.jpa.domain.Specification<Application> spec, org.springframework.data.domain.Pageable pageable);

    Optional<Application> findByPublicId(UUID publicId);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(a) FROM Application a WHERE a.status = :status AND (:eventId IS NULL OR a.event.publicId = :eventId)")
    long countByStatusAndOptionalEventId(@org.springframework.data.repository.query.Param("status") com.imperium.ims.applications.enums.ApplicationStatus status, @org.springframework.data.repository.query.Param("eventId") UUID eventId);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(a) FROM Application a WHERE a.createdAt > :dateTime AND (:eventId IS NULL OR a.event.publicId = :eventId)")
    long countByCreatedAtAfterAndOptionalEventId(@org.springframework.data.repository.query.Param("dateTime") java.time.LocalDateTime dateTime, @org.springframework.data.repository.query.Param("eventId") UUID eventId);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(a) FROM Application a WHERE a.createdAt BETWEEN :start AND :end AND (:eventId IS NULL OR a.event.publicId = :eventId)")
    long countByCreatedAtBetweenAndOptionalEventId(@org.springframework.data.repository.query.Param("start") java.time.LocalDateTime start, @org.springframework.data.repository.query.Param("end") java.time.LocalDateTime end, @org.springframework.data.repository.query.Param("eventId") UUID eventId);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(a) FROM Application a WHERE a.createdAt BETWEEN :start AND :end AND a.event.publicId IN :eventIds")
    long countByCreatedAtBetweenAndEventPublicIdIn(@org.springframework.data.repository.query.Param("start") java.time.LocalDateTime start, @org.springframework.data.repository.query.Param("end") java.time.LocalDateTime end, @org.springframework.data.repository.query.Param("eventIds") java.util.List<UUID> eventIds);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(a) FROM Application a WHERE a.createdAt BETWEEN :start AND :end AND a.status = :status AND (:eventId IS NULL OR a.event.publicId = :eventId)")
    long countByCreatedAtBetweenAndStatusAndOptionalEventId(@org.springframework.data.repository.query.Param("start") java.time.LocalDateTime start, @org.springframework.data.repository.query.Param("end") java.time.LocalDateTime end, @org.springframework.data.repository.query.Param("status") com.imperium.ims.applications.enums.ApplicationStatus status, @org.springframework.data.repository.query.Param("eventId") UUID eventId);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(a) FROM Application a WHERE a.createdAt BETWEEN :start AND :end AND a.status = :status AND a.event.publicId IN :eventIds")
    long countByCreatedAtBetweenAndStatusAndEventPublicIdIn(@org.springframework.data.repository.query.Param("start") java.time.LocalDateTime start, @org.springframework.data.repository.query.Param("end") java.time.LocalDateTime end, @org.springframework.data.repository.query.Param("status") com.imperium.ims.applications.enums.ApplicationStatus status, @org.springframework.data.repository.query.Param("eventIds") java.util.List<UUID> eventIds);

    @org.springframework.data.jpa.repository.Query("SELECT a.company, COUNT(a) FROM Application a WHERE (:eventId IS NULL OR a.event.publicId = :eventId) AND a.company IS NOT NULL AND a.company != '' GROUP BY a.company ORDER BY COUNT(a) DESC")
    java.util.List<Object[]> findTopCompanies(@org.springframework.data.repository.query.Param("eventId") UUID eventId, org.springframework.data.domain.Pageable pageable);

    @org.springframework.data.jpa.repository.Query("SELECT a.company, COUNT(a) FROM Application a WHERE a.event.publicId IN :eventIds AND a.company IS NOT NULL AND a.company != '' GROUP BY a.company ORDER BY COUNT(a) DESC")
    java.util.List<Object[]> findTopCompaniesByEventPublicIdIn(@org.springframework.data.repository.query.Param("eventIds") java.util.List<UUID> eventIds, org.springframework.data.domain.Pageable pageable);

    @org.springframework.data.jpa.repository.Query("SELECT a.city, COUNT(a) FROM Application a WHERE (:eventId IS NULL OR a.event.publicId = :eventId) AND a.city IS NOT NULL AND a.city != '' GROUP BY a.city ORDER BY COUNT(a) DESC")
    java.util.List<Object[]> findTopCities(@org.springframework.data.repository.query.Param("eventId") UUID eventId, org.springframework.data.domain.Pageable pageable);

    @org.springframework.data.jpa.repository.Query("SELECT a.city, COUNT(a) FROM Application a WHERE a.event.publicId IN :eventIds AND a.city IS NOT NULL AND a.city != '' GROUP BY a.city ORDER BY COUNT(a) DESC")
    java.util.List<Object[]> findTopCitiesByEventPublicIdIn(@org.springframework.data.repository.query.Param("eventIds") java.util.List<UUID> eventIds, org.springframework.data.domain.Pageable pageable);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(a) FROM Application a WHERE a.assignedAdmin.email = :adminEmail")
    long countByAssignedAdminEmail(@org.springframework.data.repository.query.Param("adminEmail") String adminEmail);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(a) FROM Application a WHERE a.assignedAdmin.email = :adminEmail AND a.status = :status")
    long countByAssignedAdminEmailAndStatus(@org.springframework.data.repository.query.Param("adminEmail") String adminEmail, @org.springframework.data.repository.query.Param("status") com.imperium.ims.applications.enums.ApplicationStatus status);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(a) FROM Application a WHERE a.assignedAdmin.email = :adminEmail AND a.createdAt > :dateTime")
    long countByAssignedAdminEmailAndCreatedAtAfter(@org.springframework.data.repository.query.Param("adminEmail") String adminEmail, @org.springframework.data.repository.query.Param("dateTime") java.time.LocalDateTime dateTime);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(a) FROM Application a WHERE a.assignedAdmin.email = :adminEmail AND a.reviewedAt > :dateTime AND a.status = :status")
    long countByAssignedAdminEmailAndReviewedAtAfterAndStatus(@org.springframework.data.repository.query.Param("adminEmail") String adminEmail, @org.springframework.data.repository.query.Param("dateTime") java.time.LocalDateTime dateTime, @org.springframework.data.repository.query.Param("status") com.imperium.ims.applications.enums.ApplicationStatus status);

    java.util.List<Application> findByEmail(String email);
}
