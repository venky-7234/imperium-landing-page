package com.imperium.ims.service;

import com.imperium.ims.dto.PageResponse;
import com.imperium.ims.dto.CreateInvitationRequest;
import com.imperium.ims.dto.InvitationResponse;
import com.imperium.ims.dto.RsvpRequest;
import com.imperium.ims.entity.InvitationStatus;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service contract for invitation management.
 */
public interface InvitationService {

    InvitationResponse createInvitation(CreateInvitationRequest request);

    List<InvitationResponse> createBulkInvitations(List<CreateInvitationRequest> requests);

    InvitationResponse getInvitationById(Long id);
    
    PageResponse<InvitationResponse> getAllInvitations(org.springframework.data.domain.Pageable pageable);

    InvitationResponse getInvitationByToken(String token);

    byte[] downloadInvitationPdf(Long id);

    PageResponse<InvitationResponse> getInvitationsByEventPublicId(java.util.UUID eventPublicId, Pageable pageable);

    PageResponse<InvitationResponse> getInvitationsByEventPublicIdAndStatus(java.util.UUID eventPublicId, InvitationStatus status, Pageable pageable);

    InvitationResponse sendInvitation(Long id);

    void sendAllPendingForEvent(Long eventId);

    InvitationResponse submitRsvp(String token, RsvpRequest request);

    void cancelInvitation(Long id);

    void resendInvitation(Long id);

    PageResponse<InvitationResponse> searchGuestsForEvent(
            Long eventId, String query, Pageable pageable);
}
