package com.imperium.ims.service;

import com.imperium.ims.dto.PageResponse;
import com.imperium.ims.dto.CreateUserRequest;
import com.imperium.ims.dto.UpdateUserRequest;
import com.imperium.ims.dto.UserResponse;
import org.springframework.data.domain.Pageable;

/**
 * Service contract for user management operations.
 */
public interface UserService {

    PageResponse<UserResponse> getAllUsers(Pageable pageable);

    UserResponse getUserById(Long id);

    UserResponse getUserByUsername(String username);

    UserResponse createUser(CreateUserRequest request);

    UserResponse createAdmin(CreateUserRequest request);

    UserResponse getUserProfile();

    UserResponse updateProfile(com.imperium.ims.dto.UpdateProfileRequest request);

    UserResponse updateAvatar(String avatarUrl);

    UserResponse updateUser(Long id, UpdateUserRequest request);

    void deleteUser(Long id);

    void enableUser(Long id);

    void disableUser(Long id);

    void assignRoles(Long userId, java.util.Set<Long> roleIds);

    void removeRoles(Long userId, java.util.Set<Long> roleIds);

    PageResponse<UserResponse> searchUsers(String query, Pageable pageable);
}
