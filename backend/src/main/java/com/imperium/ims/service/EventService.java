package com.imperium.ims.service;

import com.imperium.ims.dto.PageResponse;
import com.imperium.ims.dto.EventRequest;
import com.imperium.ims.dto.EventResponse;
import org.springframework.data.domain.Pageable;

/**
 * Service contract for event management.
 */
public interface EventService {

    PageResponse<EventResponse> getAllEvents(Pageable pageable);

    EventResponse getEventById(java.util.UUID publicId);

    PageResponse<EventResponse> getEventsByOrganizer(java.util.UUID organizerId, Pageable pageable);

    EventResponse createEvent(EventRequest request);

    EventResponse updateEvent(java.util.UUID publicId, EventRequest request);

    void deleteEvent(java.util.UUID publicId);

    void publishEvent(java.util.UUID publicId);

    void cancelEvent(java.util.UUID publicId);
    
    void archiveEvent(java.util.UUID publicId);
    
    void deactivateEvent(java.util.UUID publicId);

    PageResponse<EventResponse> searchEvents(String query, Pageable pageable);
}
