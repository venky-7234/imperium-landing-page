package com.imperium.ims.repository;

import com.imperium.ims.entity.Invitation;
import com.imperium.ims.entity.InvitationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link Invitation} persistence operations.
 */
@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long>,
        JpaSpecificationExecutor<Invitation> {

    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"event"})
    Page<Invitation> findAll(org.springframework.data.jpa.domain.Specification<Invitation> spec, Pageable pageable);

    Optional<Invitation> findByToken(String token);

    Page<Invitation> findByEventId(Long eventId, Pageable pageable);

    Page<Invitation> findByEventIdAndStatus(Long eventId, InvitationStatus status, Pageable pageable);
    
    List<Invitation> findByEventIdAndStatus(Long eventId, InvitationStatus status);
    
    Page<Invitation> findByEventPublicId(java.util.UUID eventPublicId, Pageable pageable);

    Page<Invitation> findByEventPublicIdAndStatus(java.util.UUID eventPublicId, InvitationStatus status, Pageable pageable);

    List<Invitation> findByGuestEmail(String guestEmail);

    List<Invitation> findByEventIdAndStatusIn(Long eventId, List<InvitationStatus> statuses);

    long countByEventIdAndStatus(Long eventId, InvitationStatus status);

    long countByEventId(Long eventId);

    boolean existsByEventIdAndGuestEmail(Long eventId, String guestEmail);

    @Query("SELECT i FROM Invitation i WHERE i.event.id = :eventId " +
           "AND (LOWER(i.guestName) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "     LOWER(i.guestEmail) LIKE LOWER(CONCAT('%', :q, '%')))")
    Page<Invitation> searchByEventAndGuest(
            @Param("eventId") Long eventId,
            @Param("q") String query,
            Pageable pageable);

    @Query("SELECT i.status, COUNT(i) FROM Invitation i WHERE i.event.id = :eventId GROUP BY i.status")
    List<Object[]> countByEventIdGroupByStatus(@Param("eventId") Long eventId);

    @Query("SELECT i.status, COUNT(i) FROM Invitation i WHERE (:eventId IS NULL OR i.event.publicId = :eventId) GROUP BY i.status")
    List<Object[]> countByOptionalEventIdGroupByStatus(@Param("eventId") java.util.UUID eventId);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(i) FROM Invitation i WHERE i.createdAt BETWEEN :start AND :end AND (:eventId IS NULL OR i.event.publicId = :eventId)")
    long countByCreatedAtBetweenAndOptionalEventId(@org.springframework.data.repository.query.Param("start") java.time.LocalDateTime start, @org.springframework.data.repository.query.Param("end") java.time.LocalDateTime end, @org.springframework.data.repository.query.Param("eventId") java.util.UUID eventId);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(i) FROM Invitation i WHERE i.createdAt BETWEEN :start AND :end AND i.event.publicId IN :eventIds")
    long countByCreatedAtBetweenAndEventPublicIdIn(@org.springframework.data.repository.query.Param("start") java.time.LocalDateTime start, @org.springframework.data.repository.query.Param("end") java.time.LocalDateTime end, @org.springframework.data.repository.query.Param("eventIds") java.util.List<java.util.UUID> eventIds);

    @Query("SELECT i.status, COUNT(i) FROM Invitation i WHERE i.event.publicId IN :eventIds GROUP BY i.status")
    List<Object[]> countByEventPublicIdInGroupByStatus(@Param("eventIds") List<java.util.UUID> eventIds);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(i) FROM Invitation i WHERE i.createdAt > :date AND (:eventId IS NULL OR i.event.publicId = :eventId)")
    long countByCreatedAtAfterAndOptionalEventId(@org.springframework.data.repository.query.Param("date") java.time.LocalDateTime date, @org.springframework.data.repository.query.Param("eventId") java.util.UUID eventId);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(i) FROM Invitation i WHERE (:eventId IS NULL OR i.event.publicId = :eventId)")
    long countByOptionalEventId(@org.springframework.data.repository.query.Param("eventId") java.util.UUID eventId);
}
