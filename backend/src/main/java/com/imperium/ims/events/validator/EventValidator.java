package com.imperium.ims.events.validator;

import com.imperium.ims.dto.EventRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Custom validator for event-specific business rules.
 */
@Component
@RequiredArgsConstructor
public class EventValidator {

    /**
     * Validates that the event end date is after the start date.
     *
     * @param request the event request to validate
     * @throws com.imperium.ims.exception.BusinessException if end is before start
     */
    public void validateEventDates(EventRequest request) {
        // TODO: Implement — endDateTime > startDateTime, rsvpDeadline <= startDateTime
    }

    /**
     * Validates that the RSVP deadline does not exceed the event start time.
     *
     * @param request the event request
     */
    public void validateRsvpDeadline(EventRequest request) {
        // TODO: Implement
    }

    /**
     * Validates that the organizer has permission to modify the event.
     *
     * @param eventId     the event to check
     * @param requesterId the current user
     */
    public void validateOwnership(Long eventId, Long requesterId) {
        // TODO: Implement
    }
}
