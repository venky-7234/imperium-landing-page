package com.imperium.ims.service;

import com.imperium.ims.dto.ApplicationResponse;
import com.imperium.ims.dto.ApplicationSearchRequest;
import com.imperium.ims.dto.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Service contract for guest application management.
 */
public interface ApplicationService {

    ApplicationResponse createApplication(com.imperium.ims.dto.CreateApplicationRequest request);

    PageResponse<ApplicationResponse> getAllApplications(Pageable pageable);

    ApplicationResponse getApplicationById(UUID publicId);

    PageResponse<ApplicationResponse> searchApplications(ApplicationSearchRequest request, Pageable pageable);

    ApplicationResponse approveApplication(UUID publicId);

    ApplicationResponse rejectApplication(UUID publicId, com.imperium.ims.dto.RejectApplicationRequest request);

    com.imperium.ims.dto.GuestProfileResponse getGuestProfile(UUID publicId);

    ApplicationResponse assignAdmin(UUID publicId, com.imperium.ims.dto.AssignAdminRequest request);

    java.util.List<ApplicationResponse> bulkApprove(com.imperium.ims.dto.BulkActionRequest request);

    java.util.List<ApplicationResponse> bulkReject(com.imperium.ims.dto.BulkActionRequest request);

    ApplicationResponse getMyApplication(String userEmail);
}
