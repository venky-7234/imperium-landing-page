package com.imperium.ims.repository;

import com.imperium.ims.common.enums.Status;
import com.imperium.ims.entity.Event;
import com.imperium.ims.entity.EventType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for {@link Event} persistence operations.
 */
@Repository
public interface EventRepository extends JpaRepository<Event, Long>,
        JpaSpecificationExecutor<Event> {

    java.util.Optional<Event> findByPublicIdAndDeletedFalse(java.util.UUID publicId);
    
    List<Event> findByPublicIdInAndDeletedFalse(java.util.Collection<java.util.UUID> publicIds);

    Page<Event> findByOrganizerPublicIdAndDeletedFalse(java.util.UUID organizerPublicId, Pageable pageable);

    Page<Event> findByOrganizerIdAndDeletedFalse(Long organizerId, Pageable pageable);

    Page<Event> findByStatusAndDeletedFalse(Status status, Pageable pageable);

    Page<Event> findByEventTypeAndDeletedFalse(EventType eventType, Pageable pageable);

    List<Event> findByStartDateTimeBetweenAndDeletedFalse(
            LocalDateTime from, LocalDateTime to);

    @Query("SELECT e FROM Event e WHERE e.deleted = false AND " +
           "(LOWER(e.title) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           " LOWER(e.venue) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           " LOWER(e.city)  LIKE LOWER(CONCAT('%', :q, '%')))")
    Page<Event> searchEvents(@Param("q") String query, Pageable pageable);

    long countByOrganizerIdAndDeletedFalse(Long organizerId);
}
