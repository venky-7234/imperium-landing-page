package com.imperium.ims.service.impl;

import com.imperium.ims.dto.ApplicationResponse;
import com.imperium.ims.dto.ApplicationSearchRequest;
import com.imperium.ims.entity.Application;
import com.imperium.ims.mapper.ApplicationMapper;
import com.imperium.ims.repository.ApplicationRepository;
import com.imperium.ims.service.ApplicationService;
import com.imperium.ims.service.AuditService;
import com.imperium.ims.dto.PageResponse;
import com.imperium.ims.exception.ResourceNotFoundException;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.imperium.ims.entity.EmailQueue;
import com.imperium.ims.repository.EmailQueueRepository;
import com.imperium.ims.entity.WhatsAppQueue;
import com.imperium.ims.repository.WhatsAppQueueRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of {@link ApplicationService} for Guest Applications.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ApplicationMapper applicationMapper;
    private final com.imperium.ims.repository.InvitationRepository invitationRepository;
    private final com.imperium.ims.repository.AuditLogRepository auditLogRepository;
    private final com.imperium.ims.service.NotificationService notificationService;
    private final com.imperium.ims.service.EmailService emailService;
    private final com.imperium.ims.service.WhatsAppService whatsAppService;
    private final com.imperium.ims.repository.WhatsAppLogRepository whatsAppLogRepository;
    private final com.imperium.ims.repository.EmailLogRepository emailLogRepository;
    private final EmailQueueRepository emailQueueRepository;
    private final WhatsAppQueueRepository whatsAppQueueRepository;
    private final com.imperium.ims.repository.EventRepository eventRepository;
    private final com.imperium.ims.repository.UserRepository userRepository;
    private final AuditService auditService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @org.springframework.cache.annotation.CacheEvict(value = {"dashboardSummary", "adminDashboardSummary"}, allEntries = true)
    public ApplicationResponse createApplication(com.imperium.ims.dto.CreateApplicationRequest request) {
        com.imperium.ims.entity.Event event;
        if (request.getEventId() != null) {
            event = eventRepository.findById(request.getEventId())
                    .orElseThrow(() -> new ResourceNotFoundException("Event", "id", request.getEventId()));
        } else {
            // Find any upcoming or active event from the database
            event = eventRepository.findAll().stream().findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("No active events found in the database. Please create an event first.", "event", null));
        }

        String[] nameParts = request.getName().trim().split("\\s+", 2);
        String firstName = nameParts[0];
        String lastName = nameParts.length > 1 ? nameParts[1] : "";

        String socialProfile = StringUtils.hasText(request.getLinkedin()) ? request.getLinkedin() : request.getInstagram();

        Application app = Application.builder()
                .event(event)
                .firstName(firstName)
                .lastName(lastName)
                .email(request.getEmail())
                .phone(request.getContactNumber())
                .socialProfileUrl(socialProfile)
                .company(request.getCompany())
                .city(request.getCity())
                .industry(request.getIndustry())
                .annualRevenue(request.getAnnualRevenue())
                .yearsInBusiness(request.getYearsInBusiness())
                .whyAttend(request.getWhyAttend())
                .whatValue(request.getWhatValue())
                .referredBy(request.getReferredBy())
                .notes(request.getWhyAttend()) // fallback or keep empty if not needed
                .status(com.imperium.ims.applications.enums.ApplicationStatus.PENDING)
                .build();
        
        app = applicationRepository.save(app);

        auditService.log(request.getEmail(), "APPLICATION_CREATED", "APPLICATION", app.getPublicId().toString(), "New application submitted by " + request.getEmail(), "SUCCESS");

        // Send confirmation email
        emailService.sendApplicationReceived(app);

        return applicationMapper.toResponse(app);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ApplicationResponse> getAllApplications(Pageable pageable) {
        boolean isSuperAdmin = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"));

        if (!isSuperAdmin) {
            ApplicationSearchRequest request = new ApplicationSearchRequest();
            return searchApplications(request, pageable);
        }

        Page<ApplicationResponse> page = applicationRepository.findAll(pageable)
                .map(applicationMapper::toResponse);
        return PageResponse.of(page);
    }

    @Override
    @Transactional(readOnly = true)
    public ApplicationResponse getApplicationById(UUID publicId) {
        Application app = applicationRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", "publicId", publicId));
        return applicationMapper.toResponse(app);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ApplicationResponse> searchApplications(ApplicationSearchRequest request, Pageable pageable) {
        Specification<Application> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Exclude soft-deleted
            predicates.add(cb.isFalse(root.get("deleted")));

            // 2. Filter by status
            if (request.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), request.getStatus()));
            }

            // 3. Filter by Event ID (UUID)
            if (request.getEventId() != null) {
                predicates.add(cb.equal(root.get("event").get("publicId"), request.getEventId()));
            }

            // 4. Keyword search (Name, Phone, Email, App ID, Inv Num, Company, LinkedIn)
            if (StringUtils.hasText(request.getQuery())) {
                String likePattern = "%" + request.getQuery().toLowerCase() + "%";
                Predicate firstNameMatch = cb.like(cb.lower(root.get("firstName")), likePattern);
                Predicate lastNameMatch = cb.like(cb.lower(root.get("lastName")), likePattern);
                Predicate emailMatch = cb.like(cb.lower(root.get("email")), likePattern);
                Predicate phoneMatch = cb.like(cb.lower(root.get("phone")), likePattern);
                Predicate companyMatch = cb.like(cb.lower(root.get("company")), likePattern);
                Predicate socialMatch = cb.like(cb.lower(root.get("socialProfileUrl")), likePattern);
                Predicate invNumMatch = cb.like(cb.lower(root.get("invitationNumber")), likePattern);
                Predicate appIdMatch = cb.like(cb.lower(cb.function("CAST", String.class, root.get("publicId"))), likePattern);
                
                predicates.add(cb.or(firstNameMatch, lastNameMatch, emailMatch, phoneMatch, companyMatch, socialMatch, invNumMatch, appIdMatch));
            }

            // 5. Additional Filters
            if (StringUtils.hasText(request.getCompany())) {
                predicates.add(cb.like(cb.lower(root.get("company")), "%" + request.getCompany().toLowerCase() + "%"));
            }
            if (StringUtils.hasText(request.getCity())) {
                predicates.add(cb.like(cb.lower(root.get("city")), "%" + request.getCity().toLowerCase() + "%"));
            }
            if (StringUtils.hasText(request.getIndustry())) {
                predicates.add(cb.equal(cb.lower(root.get("industry")), request.getIndustry().toLowerCase()));
            }
            if (StringUtils.hasText(request.getDateFrom())) {
                java.time.LocalDateTime dateFrom = java.time.LocalDate.parse(request.getDateFrom()).atStartOfDay();
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), dateFrom));
            }
            if (StringUtils.hasText(request.getDateTo())) {
                java.time.LocalDateTime dateTo = java.time.LocalDate.parse(request.getDateTo()).plusDays(1).atStartOfDay().minusNanos(1);
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), dateTo));
            }

            // 6. Assigned to Me filter (or forced for non-Admin/SuperAdmin)
            boolean isSuperAdmin = org.springframework.security.core.context.SecurityContextHolder
                    .getContext().getAuthentication().getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"));
            boolean isAdmin = org.springframework.security.core.context.SecurityContextHolder
                    .getContext().getAuthentication().getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            boolean forceAssignedToMe = !(isSuperAdmin || isAdmin);
            if (forceAssignedToMe || Boolean.TRUE.equals(request.getAssignedToMe())) {
                String currentUserEmail = com.imperium.ims.util.SecurityUtils.getCurrentUser()
                        .map(com.imperium.ims.security.UserPrincipal::getEmail)
                        .orElseThrow(() -> new com.imperium.ims.exception.BusinessException("User not authenticated"));
                
                // 6a. Admin is directly assigned to the Application
                jakarta.persistence.criteria.Join<com.imperium.ims.entity.Application, com.imperium.ims.entity.User> assignedAdminJoin = root.join("assignedAdmin", jakarta.persistence.criteria.JoinType.LEFT);
                Predicate assignedToApplication = cb.equal(assignedAdminJoin.get("email"), currentUserEmail);

                // 6b. Admin is assigned to the Event
                jakarta.persistence.criteria.Subquery<Long> subquery = query.subquery(Long.class);
                jakarta.persistence.criteria.Root<com.imperium.ims.entity.User> userRoot = subquery.from(com.imperium.ims.entity.User.class);
                jakarta.persistence.criteria.Join<com.imperium.ims.entity.User, com.imperium.ims.entity.Event> userEventsJoin = userRoot.join("assignedEvents");
                subquery.select(userEventsJoin.get("id"));
                subquery.where(cb.equal(userRoot.get("email"), currentUserEmail));
                
                Predicate assignedToEvent = root.get("event").get("id").in(subquery);
                
                predicates.add(cb.or(assignedToApplication, assignedToEvent));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<ApplicationResponse> page = applicationRepository.findAll(spec, pageable)
                .map(applicationMapper::toResponse);

        return PageResponse.of(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @org.springframework.cache.annotation.CacheEvict(value = {"dashboardSummary", "adminDashboardSummary"}, allEntries = true)
    public ApplicationResponse approveApplication(UUID publicId) {
        Application app = applicationRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", "publicId", publicId));

        if (app.getStatus() == com.imperium.ims.applications.enums.ApplicationStatus.APPROVED) {
            throw new com.imperium.ims.exception.BusinessException("Application is already approved");
        }

        String reviewerEmail = com.imperium.ims.util.SecurityUtils.getCurrentUser()
                .map(com.imperium.ims.security.UserPrincipal::getEmail)
                .orElseThrow(() -> new com.imperium.ims.exception.BusinessException("User not authenticated"));
        com.imperium.ims.entity.User reviewer = userRepository.findByEmail(reviewerEmail).orElse(null);

        app.setStatus(com.imperium.ims.applications.enums.ApplicationStatus.APPROVED);
        if (reviewer != null) {
            app.setReviewer(reviewer);
        }
        app.setReviewedAt(java.time.LocalDateTime.now());
        
        // Generate Invitation
        String token = UUID.randomUUID().toString().replace("-", "");
        String timestamp = String.valueOf(System.currentTimeMillis());
        String random = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        String invitationNumber = "INV-" + timestamp + "-" + random;

        app.setInvitationNumber(invitationNumber);
        applicationRepository.save(app);

        com.imperium.ims.entity.Invitation invitation = com.imperium.ims.entity.Invitation.builder()
                .event(app.getEvent())
                .guestName(app.getFirstName() + " " + app.getLastName())
                .guestEmail(app.getEmail())
                .guestPhone(app.getPhone())
                .guestNote(app.getNotes())
                .status(com.imperium.ims.entity.InvitationStatus.SENT)
                .channel(com.imperium.ims.common.enums.Channel.EMAIL)
                .token(token)
                .invitationNumber(invitationNumber)
                .sentAt(java.time.LocalDateTime.now())
                .build();
        invitation = invitationRepository.save(invitation);

        // Store Audit Log
        auditService.log(reviewerEmail, "APPLICATION_APPROVED", "APPLICATION", app.getPublicId().toString(), "Application approved for " + app.getEmail(), "SUCCESS");

        // Create Notification (In-app only)
        notificationService.send(
                app.getEmail(),
                "GUEST",
                "Application Approved",
                "Your application for " + app.getEvent().getTitle() + " has been approved.",
                com.imperium.ims.common.enums.Channel.EMAIL,
                invitation.getId().toString(),
                "INVITATION"
        );

        return applicationMapper.toResponse(app);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @org.springframework.cache.annotation.CacheEvict(value = {"dashboardSummary", "adminDashboardSummary"}, allEntries = true)
    public ApplicationResponse rejectApplication(UUID publicId, com.imperium.ims.dto.RejectApplicationRequest request) {
        Application app = applicationRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", "publicId", publicId));

        if (app.getStatus() == com.imperium.ims.applications.enums.ApplicationStatus.REJECTED) {
            throw new com.imperium.ims.exception.BusinessException("Application is already rejected");
        }

        String reviewerEmail = com.imperium.ims.util.SecurityUtils.getCurrentUser()
                .map(com.imperium.ims.security.UserPrincipal::getEmail)
                .orElseThrow(() -> new com.imperium.ims.exception.BusinessException("User not authenticated"));
        com.imperium.ims.entity.User reviewer = userRepository.findByEmail(reviewerEmail).orElse(null);

        app.setStatus(com.imperium.ims.applications.enums.ApplicationStatus.REJECTED);
        app.setRejectReason(request.getReason());
        if (reviewer != null) {
            app.setReviewer(reviewer);
        }
        app.setReviewedAt(java.time.LocalDateTime.now());
        applicationRepository.save(app);

        // Store Audit Log
        auditService.log(reviewerEmail, "APPLICATION_REJECTED", "APPLICATION", app.getPublicId().toString(), "Application rejected for " + app.getEmail() + ". Reason: " + request.getReason(), "SUCCESS");

        // Create Notification
        notificationService.send(
                app.getEmail(),
                "GUEST",
                "Application Update",
                "We regret to inform you that your application for " + app.getEvent().getTitle() + " could not be approved.",
                com.imperium.ims.common.enums.Channel.EMAIL,
                app.getId().toString(),
                "APPLICATION"
        );

        // Since it's rejected, send rejection email
        // emailService.sendApplicationRejected(app); // User requested manual sending
        return applicationMapper.toResponse(app);
    }

    @Override
    @Transactional(readOnly = true)
    public com.imperium.ims.dto.GuestProfileResponse getGuestProfile(UUID publicId) {
        Application baseApp = applicationRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", "publicId", publicId));

        // Admins can see all guest profiles.

        String email = baseApp.getEmail();
        String phone = baseApp.getPhone();

        List<Application> allApps = applicationRepository.findByEmail(email);
        List<com.imperium.ims.entity.Invitation> allInvs = invitationRepository.findByGuestEmail(email);
        List<com.imperium.ims.entity.EmailLog> emails = emailLogRepository.findByToAddress(email);
        List<com.imperium.ims.entity.WhatsAppLog> whatsapps = phone != null ? whatsAppLogRepository.findByToNumber(phone) : new java.util.ArrayList<>();

        List<String> types = java.util.Arrays.asList("APPLICATION", "INVITATION");
        List<com.imperium.ims.entity.AuditLog> audits = auditLogRepository.findByActorOrResourceId(email, types);

        List<com.imperium.ims.dto.GuestProfileResponse.ApplicationHistoryDto> appHistory = new ArrayList<>();
        List<com.imperium.ims.dto.GuestProfileResponse.EventHistoryDto> events = new ArrayList<>();
        List<com.imperium.ims.dto.GuestProfileResponse.TimelineItemDto> timeline = new ArrayList<>();
        List<com.imperium.ims.dto.GuestProfileResponse.ApprovalHistoryDto> approvalHistory = new ArrayList<>();
        List<com.imperium.ims.dto.GuestProfileResponse.RejectionHistoryDto> rejectionHistory = new ArrayList<>();

        for (Application app : allApps) {
            String reviewerName = app.getReviewer() != null
                    ? app.getReviewer().getFirstName() + " " + app.getReviewer().getLastName()
                    : null;

            appHistory.add(com.imperium.ims.dto.GuestProfileResponse.ApplicationHistoryDto.builder()
                    .publicId(app.getPublicId().toString())
                    .eventName(app.getEvent() != null ? app.getEvent().getTitle() : "Unknown Event")
                    .status(app.getStatus().name())
                    .rejectReason(app.getRejectReason())
                    .appliedAt(app.getCreatedAt())
                    .reviewedAt(app.getReviewedAt())
                    .reviewedBy(reviewerName)
                    .build());

            timeline.add(com.imperium.ims.dto.GuestProfileResponse.TimelineItemDto.builder()
                    .timestamp(app.getCreatedAt())
                    .title("Application Submitted")
                    .description("Applied for " + (app.getEvent() != null ? app.getEvent().getTitle() : "event"))
                    .type("APPLICATION_SUBMITTED")
                    .build());

            if (app.getStatus() == com.imperium.ims.applications.enums.ApplicationStatus.APPROVED) {
                if (app.getEvent() != null) {
                    events.add(com.imperium.ims.dto.GuestProfileResponse.EventHistoryDto.builder()
                            .eventName(app.getEvent().getTitle())
                            .role("GUEST")
                            .eventDate(app.getEvent().getStartDateTime())
                            .build());
                }
                approvalHistory.add(com.imperium.ims.dto.GuestProfileResponse.ApprovalHistoryDto.builder()
                        .eventName(app.getEvent() != null ? app.getEvent().getTitle() : "Unknown Event")
                        .approvedBy(reviewerName)
                        .approvedAt(app.getReviewedAt())
                        .invitationNumber(app.getInvitationNumber())
                        .build());
                if (app.getReviewedAt() != null) {
                    timeline.add(com.imperium.ims.dto.GuestProfileResponse.TimelineItemDto.builder()
                            .timestamp(app.getReviewedAt())
                            .title("Application Approved")
                            .description("Approved by " + (reviewerName != null ? reviewerName : "system") + " for " + (app.getEvent() != null ? app.getEvent().getTitle() : "event"))
                            .type("APPROVED")
                            .build());
                }
            } else if (app.getStatus() == com.imperium.ims.applications.enums.ApplicationStatus.REJECTED) {
                rejectionHistory.add(com.imperium.ims.dto.GuestProfileResponse.RejectionHistoryDto.builder()
                        .eventName(app.getEvent() != null ? app.getEvent().getTitle() : "Unknown Event")
                        .rejectedBy(reviewerName)
                        .rejectedAt(app.getReviewedAt())
                        .reason(app.getRejectReason())
                        .build());
                if (app.getReviewedAt() != null) {
                    timeline.add(com.imperium.ims.dto.GuestProfileResponse.TimelineItemDto.builder()
                            .timestamp(app.getReviewedAt())
                            .title("Application Rejected")
                            .description("Rejected" + (app.getRejectReason() != null ? ": " + app.getRejectReason() : "") + " for " + (app.getEvent() != null ? app.getEvent().getTitle() : "event"))
                            .type("REJECTED")
                            .build());
                }
            }
        }

        List<com.imperium.ims.dto.GuestProfileResponse.InvitationHistoryDto> invHistory = new ArrayList<>();
        // Get invitation status from the most recent invitation for this application
        String currentInvStatus = null;
        String currentInvNumber = baseApp.getInvitationNumber();
        for (com.imperium.ims.entity.Invitation inv : allInvs) {
            if (currentInvStatus == null) currentInvStatus = inv.getStatus().name();
            invHistory.add(com.imperium.ims.dto.GuestProfileResponse.InvitationHistoryDto.builder()
                    .invitationNumber(inv.getInvitationNumber())
                    .eventName(inv.getEvent() != null ? inv.getEvent().getTitle() : "Unknown Event")
                    .status(inv.getStatus().name())
                    .issuedAt(inv.getCreatedAt())
                    .build());

            timeline.add(com.imperium.ims.dto.GuestProfileResponse.TimelineItemDto.builder()
                    .timestamp(inv.getCreatedAt())
                    .title("Invitation Generated")
                    .description("Invitation " + inv.getInvitationNumber() + " generated for " + (inv.getEvent() != null ? inv.getEvent().getTitle() : "event"))
                    .type("INVITATION_GENERATED")
                    .build());
        }

        List<com.imperium.ims.dto.GuestProfileResponse.CommunicationHistoryDto> commHistory = new ArrayList<>();
        for (com.imperium.ims.entity.EmailLog e : emails) {
            commHistory.add(com.imperium.ims.dto.GuestProfileResponse.CommunicationHistoryDto.builder()
                    .type("EMAIL")
                    .status(e.getStatus())
                    .subjectOrPreview(e.getSubject())
                    .sentAt(e.getCreatedAt())
                    .build());
            timeline.add(com.imperium.ims.dto.GuestProfileResponse.TimelineItemDto.builder()
                    .timestamp(e.getCreatedAt())
                    .title("Email Sent")
                    .description("Subject: " + e.getSubject())
                    .type("EMAIL_SENT")
                    .build());
        }
        for (com.imperium.ims.entity.WhatsAppLog w : whatsapps) {
            commHistory.add(com.imperium.ims.dto.GuestProfileResponse.CommunicationHistoryDto.builder()
                    .type("WHATSAPP")
                    .status(w.getStatus())
                    .subjectOrPreview(w.getMessageBody() != null && w.getMessageBody().length() > 30 ? w.getMessageBody().substring(0, 30) + "..." : w.getMessageBody())
                    .sentAt(w.getCreatedAt())
                    .build());
            timeline.add(com.imperium.ims.dto.GuestProfileResponse.TimelineItemDto.builder()
                    .timestamp(w.getCreatedAt())
                    .title("WhatsApp Sent")
                    .description("Status: " + w.getStatus())
                    .type("WHATSAPP_SENT")
                    .build());
        }

        List<com.imperium.ims.dto.GuestProfileResponse.AuditHistoryDto> auditHistoryList = new ArrayList<>();
        for (com.imperium.ims.entity.AuditLog a : audits) {
            auditHistoryList.add(com.imperium.ims.dto.GuestProfileResponse.AuditHistoryDto.builder()
                    .action(a.getAction())
                    .actor(a.getCreatedBy())
                    .description(a.getDescription())
                    .timestamp(a.getCreatedAt())
                    .build());
        }

        // Sort timeline descending
        timeline.sort((t1, t2) -> t2.getTimestamp().compareTo(t1.getTimestamp()));

        return com.imperium.ims.dto.GuestProfileResponse.builder()
                .firstName(baseApp.getFirstName())
                .lastName(baseApp.getLastName())
                .email(baseApp.getEmail())
                .phone(baseApp.getPhone())
                .company(baseApp.getCompany())
                .designation(baseApp.getDesignation())
                .industry(baseApp.getIndustry())
                .linkedin(baseApp.getSocialProfileUrl())
                .city(baseApp.getCity())
                .annualRevenue(baseApp.getAnnualRevenue())
                .yearsInBusiness(baseApp.getYearsInBusiness())
                .whyAttend(baseApp.getWhyAttend())
                .whatValue(baseApp.getWhatValue())
                .referredBy(baseApp.getReferredBy())
                .notes(baseApp.getNotes())
                .currentApplicationId(baseApp.getPublicId().toString())
                .currentApplicationStatus(baseApp.getStatus().name())
                .currentEventName(baseApp.getEvent() != null ? baseApp.getEvent().getTitle() : null)
                .currentEventId(baseApp.getEvent() != null ? baseApp.getEvent().getPublicId().toString() : null)
                .invitationStatus(currentInvStatus)
                .invitationNumber(currentInvNumber)
                .applicationHistory(appHistory)
                .invitationHistory(invHistory)
                .eventsAttended(events)
                .communicationHistory(commHistory)
                .auditHistory(auditHistoryList)
                .timeline(timeline)
                .approvalHistory(approvalHistory)
                .rejectionHistory(rejectionHistory)
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @org.springframework.cache.annotation.CacheEvict(value = {"dashboardSummary", "adminDashboardSummary"}, allEntries = true)
    public ApplicationResponse assignAdmin(UUID publicId, com.imperium.ims.dto.AssignAdminRequest request) {
        Application app = applicationRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", "publicId", publicId));

        com.imperium.ims.entity.User adminToAssign;
        try {
            UUID adminUuid = UUID.fromString(request.getAdminIdentifier());
            adminToAssign = userRepository.findByPublicId(adminUuid)
                    .orElseThrow(() -> new ResourceNotFoundException("Admin", "publicId", adminUuid));
        } catch (IllegalArgumentException e) {
            adminToAssign = userRepository.findByEmail(request.getAdminIdentifier())
                    .orElseThrow(() -> new ResourceNotFoundException("Admin", "email", request.getAdminIdentifier()));
        }

        app.setAssignedAdmin(adminToAssign);
        applicationRepository.save(app);

        // Notify assigned admin
        String adminIdStr = adminToAssign.getId().toString();
        String guestName = app.getFirstName() + " " + app.getLastName();
        notificationService.send(
                adminIdStr,
                "USER",
                "Guest Assigned",
                "You have been assigned to manage guest application for " + guestName + ".",
                com.imperium.ims.common.enums.Channel.IN_APP,
                app.getPublicId().toString(),
                "APPLICATION"
        );

        // Store Audit Log
        com.imperium.ims.entity.AuditLog auditLog = com.imperium.ims.entity.AuditLog.builder()
                .action("APPLICATION_ASSIGNED")
                .resourceType("APPLICATION")
                .resourceId(app.getPublicId().toString())
                .description("Application assigned to " + adminToAssign.getEmail())
                .status("SUCCESS")
                .build();
        auditLogRepository.save(auditLog);

        return applicationMapper.toResponse(app);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<ApplicationResponse> bulkApprove(com.imperium.ims.dto.BulkActionRequest request) {
        List<ApplicationResponse> responses = new ArrayList<>();
        for (UUID id : request.getPublicIds()) {
            try {
                responses.add(approveApplication(id));
            } catch (Exception e) {
                log.error("Failed to approve application {}", id, e);
            }
        }
        return responses;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<ApplicationResponse> bulkReject(com.imperium.ims.dto.BulkActionRequest request) {
        List<ApplicationResponse> responses = new ArrayList<>();
        com.imperium.ims.dto.RejectApplicationRequest rejectRequest = new com.imperium.ims.dto.RejectApplicationRequest();
        rejectRequest.setReason(request.getReason());
        
        for (UUID id : request.getPublicIds()) {
            try {
                responses.add(rejectApplication(id, rejectRequest));
            } catch (Exception e) {
                log.error("Failed to reject application {}", id, e);
            }
        }
        return responses;
    }

    @Override
    @Transactional(readOnly = true)
    public ApplicationResponse getMyApplication(String userEmail) {
        java.util.List<Application> apps = applicationRepository.findByEmail(userEmail);
        if (apps == null || apps.isEmpty()) {
            throw new com.imperium.ims.exception.ResourceNotFoundException("Application", "email", userEmail);
        }
        return applicationMapper.toResponse(apps.get(0));
    }
}
