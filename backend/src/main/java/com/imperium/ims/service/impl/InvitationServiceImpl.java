package com.imperium.ims.service.impl;

import com.imperium.ims.dto.PageResponse;
import com.imperium.ims.entity.Event;
import com.imperium.ims.repository.EventRepository;
import com.imperium.ims.exception.ResourceNotFoundException;
import com.imperium.ims.dto.CreateInvitationRequest;
import com.imperium.ims.dto.InvitationResponse;
import com.imperium.ims.dto.RsvpRequest;
import com.imperium.ims.entity.Invitation;
import com.imperium.ims.entity.InvitationStatus;
import com.imperium.ims.mapper.InvitationMapper;
import com.imperium.ims.repository.InvitationRepository;
import com.imperium.ims.service.InvitationService;
import com.imperium.ims.service.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of {@link InvitationService}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class InvitationServiceImpl implements InvitationService {

    private final InvitationRepository invitationRepository;
    private final EventRepository eventRepository;
    private final InvitationMapper invitationMapper;
    private final com.imperium.ims.service.PdfGenerationService pdfGenerationService;
    private final AuditService auditService;
    private final com.imperium.ims.service.EmailService emailService;

    @Override
    public InvitationResponse createInvitation(CreateInvitationRequest request) {
        // TODO: Full business logic — validate capacity, check duplicate, dispatch notification
        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", request.getEventId()));

        Invitation invitation = invitationMapper.toEntity(request);
        invitation.setEvent(event);
        invitation.setToken(generateToken());
        invitation.setInvitationNumber(generateInvitationNumber());
        invitation.setStatus(InvitationStatus.GENERATED);

        invitation = invitationRepository.save(invitation);
        
        auditService.log("SYSTEM", "INVITATION_GENERATED", "INVITATION", invitation.getId().toString(), "Generated invitation for " + request.getGuestEmail(), "SUCCESS");

        return invitationMapper.toResponse(invitation);
    }

    @Override
    public List<InvitationResponse> createBulkInvitations(List<CreateInvitationRequest> requests) {
        // TODO: Implement — batch persist, async dispatch
        return requests.stream().map(this::createInvitation).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public InvitationResponse getInvitationById(Long id) {
        Invitation invitation = invitationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invitation", "id", id));
        return invitationMapper.toResponse(invitation);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<InvitationResponse> getAllInvitations(org.springframework.data.domain.Pageable pageable) {
        boolean isSuperAdmin = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"));

        org.springframework.data.jpa.domain.Specification<Invitation> spec = (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new java.util.ArrayList<>();
            if (!isSuperAdmin) {
                String currentUserEmail = com.imperium.ims.util.SecurityUtils.getCurrentUser()
                        .map(com.imperium.ims.security.UserPrincipal::getEmail)
                        .orElseThrow(() -> new com.imperium.ims.exception.BusinessException("User not authenticated"));
                
                jakarta.persistence.criteria.Subquery<Long> subquery = query.subquery(Long.class);
                jakarta.persistence.criteria.Root<com.imperium.ims.entity.User> userRoot = subquery.from(com.imperium.ims.entity.User.class);
                jakarta.persistence.criteria.Join<com.imperium.ims.entity.User, com.imperium.ims.entity.Event> userEventsJoin = userRoot.join("assignedEvents");
                subquery.select(userEventsJoin.get("id"));
                subquery.where(cb.equal(userRoot.get("email"), currentUserEmail));
                
                predicates.add(root.get("event").get("id").in(subquery));
            }
            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        Page<Invitation> page = invitationRepository.findAll(spec, pageable);
        return PageResponse.of(page.map(invitationMapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public InvitationResponse getInvitationByToken(String token) {
        Invitation invitation = invitationRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invitation", "token", token));
        return invitationMapper.toResponse(invitation);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<InvitationResponse> getInvitationsByEventPublicId(java.util.UUID eventPublicId, Pageable pageable) {
        Page<InvitationResponse> page = invitationRepository
                .findByEventPublicId(eventPublicId, pageable)
                .map(invitationMapper::toResponse);
        return PageResponse.of(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<InvitationResponse> getInvitationsByEventPublicIdAndStatus(
            java.util.UUID eventPublicId, InvitationStatus status, Pageable pageable) {
        Page<InvitationResponse> page = invitationRepository
                .findByEventPublicIdAndStatus(eventPublicId, status, pageable)
                .map(invitationMapper::toResponse);
        return PageResponse.of(page);
    }

    @Override
    public InvitationResponse sendInvitation(Long id) {
        Invitation invitation = invitationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invitation", "id", id));
        invitation.setStatus(InvitationStatus.SENT);
        invitation.setSentAt(LocalDateTime.now());
        invitation = invitationRepository.save(invitation);
        
        emailService.sendInvitationEmail(invitation.getId());
        
        auditService.log("SYSTEM", "INVITATION_SENT", "INVITATION", invitation.getId().toString(), "Sent invitation to " + invitation.getGuestEmail(), "SUCCESS");
        
        return invitationMapper.toResponse(invitation);
    }

    @Override
    public void sendAllPendingForEvent(Long eventId) {
        List<Invitation> drafts = invitationRepository.findByEventIdAndStatus(eventId, InvitationStatus.GENERATED);
        for (Invitation invitation : drafts) {
            invitation.setStatus(InvitationStatus.SENT);
            invitation.setSentAt(LocalDateTime.now());
            invitationRepository.save(invitation);
            emailService.sendInvitationEmail(invitation.getId());
            auditService.log("SYSTEM", "INVITATION_SENT", "INVITATION", invitation.getId().toString(), "Batch sent invitation to " + invitation.getGuestEmail(), "SUCCESS");
        }
    }

    @Override
    public InvitationResponse submitRsvp(String token, RsvpRequest request) {
        // TODO: Validate token not expired, map RsvpResponse to InvitationStatus, update entity
        Invitation invitation = invitationRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invitation", "token", token));
        invitation.setRespondedAt(LocalDateTime.now());
        invitation.setResponseNote(request.getResponseNote());
        invitation.setAdditionalGuests(request.getAdditionalGuests());
        return invitationMapper.toResponse(invitationRepository.save(invitation));
    }

    @Override
    public void cancelInvitation(Long id) {
        Invitation invitation = invitationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invitation", "id", id));
        invitation.setStatus(InvitationStatus.CANCELLED);
        invitationRepository.save(invitation);
    }

    @Override
    public void resendInvitation(Long id) {
        Invitation invitation = invitationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invitation", "id", id));
        invitation.setSentAt(LocalDateTime.now());
        invitationRepository.save(invitation);
        emailService.sendInvitationEmail(invitation.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<InvitationResponse> searchGuestsForEvent(
            Long eventId, String query, Pageable pageable) {
        Page<InvitationResponse> page = invitationRepository
                .searchByEventAndGuest(eventId, query, pageable)
                .map(invitationMapper::toResponse);
        return PageResponse.of(page);
    }

    // ── Private Helpers ────────────────────────────────────────────────────

    private String generateToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private String generateInvitationNumber() {
        // Simple generation logic: INV-[timestamp]-[random 4 chars]
        String timestamp = String.valueOf(System.currentTimeMillis());
        String random = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return "INV-" + timestamp + "-" + random;
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] downloadInvitationPdf(Long id) {
        Invitation invitation = invitationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invitation", "id", id));
        return pdfGenerationService.generateInvitationPdf(invitation);
    }
}
