package com.imperium.ims.mapper;

import com.imperium.ims.dto.CreateInvitationRequest;
import com.imperium.ims.dto.InvitationResponse;
import com.imperium.ims.entity.Event;
import com.imperium.ims.entity.Invitation;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-20T20:16:55+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class InvitationMapperImpl implements InvitationMapper {

    @Override
    public InvitationResponse toResponse(Invitation invitation) {
        if ( invitation == null ) {
            return null;
        }

        InvitationResponse invitationResponse = new InvitationResponse();

        invitationResponse.setEventId( invitationEventId( invitation ) );
        invitationResponse.setEventTitle( invitationEventTitle( invitation ) );
        invitationResponse.setId( invitation.getId() );
        invitationResponse.setGuestName( invitation.getGuestName() );
        invitationResponse.setGuestEmail( invitation.getGuestEmail() );
        invitationResponse.setGuestPhone( invitation.getGuestPhone() );
        invitationResponse.setGuestNote( invitation.getGuestNote() );
        invitationResponse.setCompany( invitation.getCompany() );
        invitationResponse.setDesignation( invitation.getDesignation() );
        invitationResponse.setInvitationNumber( invitation.getInvitationNumber() );
        invitationResponse.setStatus( invitation.getStatus() );
        invitationResponse.setChannel( invitation.getChannel() );
        invitationResponse.setToken( invitation.getToken() );
        invitationResponse.setSentAt( invitation.getSentAt() );
        invitationResponse.setViewedAt( invitation.getViewedAt() );
        invitationResponse.setRespondedAt( invitation.getRespondedAt() );
        invitationResponse.setExpiresAt( invitation.getExpiresAt() );
        invitationResponse.setResponseNote( invitation.getResponseNote() );
        invitationResponse.setAdditionalGuests( invitation.getAdditionalGuests() );
        invitationResponse.setDeliveryFailed( invitation.isDeliveryFailed() );
        invitationResponse.setDeliveryError( invitation.getDeliveryError() );
        invitationResponse.setCreatedAt( invitation.getCreatedAt() );
        invitationResponse.setUpdatedAt( invitation.getUpdatedAt() );

        return invitationResponse;
    }

    @Override
    public Invitation toEntity(CreateInvitationRequest request) {
        if ( request == null ) {
            return null;
        }

        Invitation invitation = new Invitation();

        invitation.setGuestName( request.getGuestName() );
        invitation.setGuestEmail( request.getGuestEmail() );
        invitation.setGuestPhone( request.getGuestPhone() );
        invitation.setGuestNote( request.getGuestNote() );
        invitation.setCompany( request.getCompany() );
        invitation.setDesignation( request.getDesignation() );
        invitation.setChannel( request.getChannel() );
        invitation.setExpiresAt( request.getExpiresAt() );

        return invitation;
    }

    private Long invitationEventId(Invitation invitation) {
        if ( invitation == null ) {
            return null;
        }
        Event event = invitation.getEvent();
        if ( event == null ) {
            return null;
        }
        Long id = event.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String invitationEventTitle(Invitation invitation) {
        if ( invitation == null ) {
            return null;
        }
        Event event = invitation.getEvent();
        if ( event == null ) {
            return null;
        }
        String title = event.getTitle();
        if ( title == null ) {
            return null;
        }
        return title;
    }
}
