package com.imperium.ims.mapper;

import com.imperium.ims.dto.EventRequest;
import com.imperium.ims.dto.EventResponse;
import com.imperium.ims.entity.Event;
import com.imperium.ims.entity.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-20T20:16:55+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class EventMapperImpl implements EventMapper {

    @Override
    public EventResponse toResponse(Event event) {
        if ( event == null ) {
            return null;
        }

        EventResponse eventResponse = new EventResponse();

        eventResponse.setOrganizerId( eventOrganizerId( event ) );
        eventResponse.setPublicId( event.getPublicId() );
        eventResponse.setTitle( event.getTitle() );
        eventResponse.setDescription( event.getDescription() );
        eventResponse.setVenue( event.getVenue() );
        eventResponse.setVenueAddress( event.getVenueAddress() );
        eventResponse.setCity( event.getCity() );
        eventResponse.setCountry( event.getCountry() );
        eventResponse.setStartDateTime( event.getStartDateTime() );
        eventResponse.setEndDateTime( event.getEndDateTime() );
        eventResponse.setMaxGuests( event.getMaxGuests() );
        eventResponse.setRsvpDeadline( event.getRsvpDeadline() );
        eventResponse.setStatus( event.getStatus() );
        eventResponse.setEventType( event.getEventType() );
        eventResponse.setCoverImageUrl( event.getCoverImageUrl() );
        eventResponse.setPublic( event.isPublic() );
        eventResponse.setTheme( event.getTheme() );
        eventResponse.setLandingPageUrl( event.getLandingPageUrl() );
        eventResponse.setApplicationFormUrl( event.getApplicationFormUrl() );
        eventResponse.setRegistrationStart( event.getRegistrationStart() );
        eventResponse.setRegistrationEnd( event.getRegistrationEnd() );
        eventResponse.setLogoUrl( event.getLogoUrl() );
        eventResponse.setBannerUrl( event.getBannerUrl() );
        eventResponse.setInvitationTemplate( event.getInvitationTemplate() );
        eventResponse.setEmailTemplate( event.getEmailTemplate() );
        eventResponse.setWhatsappTemplate( event.getWhatsappTemplate() );
        eventResponse.setCreatedAt( event.getCreatedAt() );
        eventResponse.setUpdatedAt( event.getUpdatedAt() );

        eventResponse.setOrganizerName( event.getOrganizer() != null ? event.getOrganizer().getFirstName() + ' ' + event.getOrganizer().getLastName() : null );

        return eventResponse;
    }

    @Override
    public Event toEntity(EventRequest request) {
        if ( request == null ) {
            return null;
        }

        Event event = new Event();

        event.setTitle( request.getTitle() );
        event.setDescription( request.getDescription() );
        event.setVenue( request.getVenue() );
        event.setVenueAddress( request.getVenueAddress() );
        event.setCity( request.getCity() );
        event.setCountry( request.getCountry() );
        event.setStartDateTime( request.getStartDateTime() );
        event.setEndDateTime( request.getEndDateTime() );
        event.setMaxGuests( request.getMaxGuests() );
        event.setRsvpDeadline( request.getRsvpDeadline() );
        event.setEventType( request.getEventType() );
        event.setCoverImageUrl( request.getCoverImageUrl() );
        event.setTheme( request.getTheme() );
        event.setLandingPageUrl( request.getLandingPageUrl() );
        event.setApplicationFormUrl( request.getApplicationFormUrl() );
        event.setRegistrationStart( request.getRegistrationStart() );
        event.setRegistrationEnd( request.getRegistrationEnd() );
        event.setLogoUrl( request.getLogoUrl() );
        event.setBannerUrl( request.getBannerUrl() );
        event.setInvitationTemplate( request.getInvitationTemplate() );
        event.setEmailTemplate( request.getEmailTemplate() );
        event.setWhatsappTemplate( request.getWhatsappTemplate() );
        event.setPublic( request.isPublic() );

        return event;
    }

    @Override
    public void updateEntity(EventRequest request, Event event) {
        if ( request == null ) {
            return;
        }

        if ( request.getTitle() != null ) {
            event.setTitle( request.getTitle() );
        }
        if ( request.getDescription() != null ) {
            event.setDescription( request.getDescription() );
        }
        if ( request.getVenue() != null ) {
            event.setVenue( request.getVenue() );
        }
        if ( request.getVenueAddress() != null ) {
            event.setVenueAddress( request.getVenueAddress() );
        }
        if ( request.getCity() != null ) {
            event.setCity( request.getCity() );
        }
        if ( request.getCountry() != null ) {
            event.setCountry( request.getCountry() );
        }
        if ( request.getStartDateTime() != null ) {
            event.setStartDateTime( request.getStartDateTime() );
        }
        if ( request.getEndDateTime() != null ) {
            event.setEndDateTime( request.getEndDateTime() );
        }
        if ( request.getMaxGuests() != null ) {
            event.setMaxGuests( request.getMaxGuests() );
        }
        if ( request.getRsvpDeadline() != null ) {
            event.setRsvpDeadline( request.getRsvpDeadline() );
        }
        if ( request.getEventType() != null ) {
            event.setEventType( request.getEventType() );
        }
        if ( request.getCoverImageUrl() != null ) {
            event.setCoverImageUrl( request.getCoverImageUrl() );
        }
        if ( request.getTheme() != null ) {
            event.setTheme( request.getTheme() );
        }
        if ( request.getLandingPageUrl() != null ) {
            event.setLandingPageUrl( request.getLandingPageUrl() );
        }
        if ( request.getApplicationFormUrl() != null ) {
            event.setApplicationFormUrl( request.getApplicationFormUrl() );
        }
        if ( request.getRegistrationStart() != null ) {
            event.setRegistrationStart( request.getRegistrationStart() );
        }
        if ( request.getRegistrationEnd() != null ) {
            event.setRegistrationEnd( request.getRegistrationEnd() );
        }
        if ( request.getLogoUrl() != null ) {
            event.setLogoUrl( request.getLogoUrl() );
        }
        if ( request.getBannerUrl() != null ) {
            event.setBannerUrl( request.getBannerUrl() );
        }
        if ( request.getInvitationTemplate() != null ) {
            event.setInvitationTemplate( request.getInvitationTemplate() );
        }
        if ( request.getEmailTemplate() != null ) {
            event.setEmailTemplate( request.getEmailTemplate() );
        }
        if ( request.getWhatsappTemplate() != null ) {
            event.setWhatsappTemplate( request.getWhatsappTemplate() );
        }
        event.setPublic( request.isPublic() );
    }

    private Long eventOrganizerId(Event event) {
        if ( event == null ) {
            return null;
        }
        User organizer = event.getOrganizer();
        if ( organizer == null ) {
            return null;
        }
        Long id = organizer.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
