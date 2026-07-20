package com.imperium.ims.service;

import com.imperium.ims.dto.PageResponse;
import com.imperium.ims.dto.CreateUserRequest;
import com.imperium.ims.dto.UpdateUserRequest;
import com.imperium.ims.dto.UserResponse;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface SuperAdminService {
    UserResponse createAdmin(CreateUserRequest request);
    UserResponse createUser(CreateUserRequest request);
    UserResponse updateUser(Long id, UpdateUserRequest request);
    void activateAccount(Long id);
    void deactivateAccount(Long id);
    void deleteUser(Long id);
    void assignRoles(Long id, Set<Long> roleIds);
    void blockAccount(Long id);
    void unblockAccount(Long id);

    PageResponse<com.imperium.ims.entity.LoginHistory> getUserLoginHistory(Long userId, Pageable pageable);
    PageResponse<com.imperium.ims.entity.AuditLog> getAdminActivities(Pageable pageable);
}
