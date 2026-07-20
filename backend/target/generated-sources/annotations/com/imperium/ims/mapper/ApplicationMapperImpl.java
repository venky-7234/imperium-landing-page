package com.imperium.ims.mapper;

import com.imperium.ims.dto.ApplicationResponse;
import com.imperium.ims.entity.Application;
import com.imperium.ims.entity.Event;
import com.imperium.ims.entity.User;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-20T20:16:55+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class ApplicationMapperImpl implements ApplicationMapper {

    @Override
    public ApplicationResponse toResponse(Application application) {
        if ( application == null ) {
            return null;
        }

        ApplicationResponse applicationResponse = new ApplicationResponse();

        applicationResponse.setEventId( applicationEventPublicId( application ) );
        applicationResponse.setEventTitle( applicationEventTitle( application ) );
        UUID publicId1 = applicationAssignedAdminPublicId( application );
        if ( publicId1 != null ) {
            applicationResponse.setAssignedAdminId( publicId1.toString() );
        }
        applicationResponse.setPublicId( application.getPublicId() );
        applicationResponse.setFirstName( application.getFirstName() );
        applicationResponse.setLastName( application.getLastName() );
        applicationResponse.setEmail( application.getEmail() );
        applicationResponse.setPhone( application.getPhone() );
        applicationResponse.setSocialProfileUrl( application.getSocialProfileUrl() );
        applicationResponse.setNotes( application.getNotes() );
        applicationResponse.setCity( application.getCity() );
        applicationResponse.setCompany( application.getCompany() );
        applicationResponse.setIndustry( application.getIndustry() );
        applicationResponse.setAnnualRevenue( application.getAnnualRevenue() );
        applicationResponse.setYearsInBusiness( application.getYearsInBusiness() );
        applicationResponse.setWhyAttend( application.getWhyAttend() );
        applicationResponse.setWhatValue( application.getWhatValue() );
        applicationResponse.setRejectReason( application.getRejectReason() );
        applicationResponse.setReferredBy( application.getReferredBy() );
        applicationResponse.setStatus( application.getStatus() );
        applicationResponse.setInvitationNumber( application.getInvitationNumber() );
        applicationResponse.setCreatedAt( application.getCreatedAt() );

        applicationResponse.setAssignedAdminName( application.getAssignedAdmin() != null ? application.getAssignedAdmin().getFirstName() + ' ' + application.getAssignedAdmin().getLastName() : null );

        return applicationResponse;
    }

    private UUID applicationEventPublicId(Application application) {
        if ( application == null ) {
            return null;
        }
        Event event = application.getEvent();
        if ( event == null ) {
            return null;
        }
        UUID publicId = event.getPublicId();
        if ( publicId == null ) {
            return null;
        }
        return publicId;
    }

    private String applicationEventTitle(Application application) {
        if ( application == null ) {
            return null;
        }
        Event event = application.getEvent();
        if ( event == null ) {
            return null;
        }
        String title = event.getTitle();
        if ( title == null ) {
            return null;
        }
        return title;
    }

    private UUID applicationAssignedAdminPublicId(Application application) {
        if ( application == null ) {
            return null;
        }
        User assignedAdmin = application.getAssignedAdmin();
        if ( assignedAdmin == null ) {
            return null;
        }
        UUID publicId = assignedAdmin.getPublicId();
        if ( publicId == null ) {
            return null;
        }
        return publicId;
    }
}
