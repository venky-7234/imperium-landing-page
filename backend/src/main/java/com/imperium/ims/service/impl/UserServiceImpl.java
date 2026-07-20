package com.imperium.ims.service.impl;

import com.imperium.ims.dto.PageResponse;
import com.imperium.ims.exception.DuplicateResourceException;
import com.imperium.ims.exception.ResourceNotFoundException;
import com.imperium.ims.entity.Role;
import com.imperium.ims.repository.RoleRepository;
import com.imperium.ims.dto.CreateUserRequest;
import com.imperium.ims.dto.UpdateUserRequest;
import com.imperium.ims.dto.UserResponse;
import com.imperium.ims.entity.User;
import com.imperium.ims.mapper.UserMapper;
import com.imperium.ims.repository.UserRepository;
import com.imperium.ims.service.UserService;
import com.imperium.ims.service.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.imperium.ims.repository.EventRepository;
import com.imperium.ims.service.NotificationService;
import com.imperium.ims.common.enums.Channel;
import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of {@link UserService}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final EventRepository eventRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final AuditService auditService;
    private final NotificationService notificationService;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserResponse> getAllUsers(Pageable pageable) {
        Page<UserResponse> page = userRepository.findAll(pageable)
                .map(userMapper::toResponse);
        return PageResponse.of(page);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }
        User user = userMapper.toEntity(request);
        user.setUsername(request.getEmail().split("@")[0]);
        user.setPassword("[GOOGLE_SSO_ONLY]");
        
        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            Set<Role> roles = new HashSet<>(roleRepository.findAllById(request.getRoleIds()));
            user.setRoles(roles);
        }
        
        if (request.getAssignedEventIds() != null && !request.getAssignedEventIds().isEmpty()) {
            user.setAssignedEvents(new HashSet<>(eventRepository.findByPublicIdInAndDeletedFalse(request.getAssignedEventIds())));
        }

        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('SUPER_ADMIN')")
    public UserResponse createAdmin(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }
        User user = userMapper.toEntity(request);
        user.setUsername(request.getEmail().split("@")[0]);
        user.setPassword("[GOOGLE_SSO_ONLY]");
        
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", "ADMIN"));
        
        user.setRoles(Set.of(adminRole));

        if (request.getAssignedEventIds() != null && !request.getAssignedEventIds().isEmpty()) {
            user.setAssignedEvents(new HashSet<>(eventRepository.findByPublicIdInAndDeletedFalse(request.getAssignedEventIds())));
            
            // Notify the new admin
            notificationService.send(
                    user.getId().toString(),
                    "USER",
                    "Event Assigned",
                    "You have been assigned to manage new events. Please check your assigned events.",
                    Channel.IN_APP,
                    user.getId().toString(),
                    "USER"
            );
        }

        user = userRepository.save(user);
        
        String currentUserEmail = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        auditService.log(currentUserEmail, "ADMIN_CREATED", "USER", user.getId().toString(), "Created admin user " + user.getUsername(), "SUCCESS");

        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserProfile() {
        Long currentUserId = com.imperium.ims.util.SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new com.imperium.ims.exception.BusinessException("No authenticated user found");
        }
        if (currentUserId >= 0L && currentUserId <= 2L) {
            boolean isUser = currentUserId == 2L;
            boolean isAdmin = currentUserId == 1L;
            String mockRole = isUser ? "ROLE_USER" : (isAdmin ? "ROLE_ADMIN" : "ROLE_SUPER_ADMIN");
            String mockLastName = isUser ? "User" : (isAdmin ? "Admin" : "Superadmin");
            String mockEmail = isUser ? "dev-user@imperium.com" : (isAdmin ? "dev-admin@imperium.com" : "dev-superadmin@imperium.com");

            com.imperium.ims.dto.RoleResponse rr = new com.imperium.ims.dto.RoleResponse();
            rr.setId(currentUserId + 1L);
            rr.setName(mockRole);
            
            com.imperium.ims.dto.UserResponse ur = new com.imperium.ims.dto.UserResponse();
            ur.setId(currentUserId);
            ur.setFirstName("Dev");
            ur.setLastName(mockLastName);
            ur.setEmail(mockEmail);
            ur.setRoles(java.util.Collections.singleton(rr));
            ur.setEnabled(true);
            
            return ur;
        }
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUserId));
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse updateProfile(com.imperium.ims.dto.UpdateProfileRequest request) {
        Long currentUserId = com.imperium.ims.util.SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new com.imperium.ims.exception.BusinessException("No authenticated user found");
        }
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUserId));
        
        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());
        
        user = userRepository.save(user);
        auditService.log(user.getUsername(), "PROFILE_UPDATED", "USER", user.getId().toString(), "Updated profile information", "SUCCESS");
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse updateAvatar(String avatarUrl) {
        Long currentUserId = com.imperium.ims.util.SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new com.imperium.ims.exception.BusinessException("No authenticated user found");
        }
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUserId));
        
        user.setAvatarUrl(avatarUrl);
        user = userRepository.save(user);
        auditService.log(user.getUsername(), "PROFILE_UPDATED", "USER", user.getId().toString(), "Updated profile avatar", "SUCCESS");
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        checkSuperAdminPrivileges(user);
        userMapper.updateEntity(request, user);

        if (request.getRoleIds() != null) {
            Set<Role> roles = new HashSet<>(roleRepository.findAllById(request.getRoleIds()));
            user.setRoles(roles);
        }

        if (request.getAssignedEventIds() != null) {
            user.setAssignedEvents(new HashSet<>(eventRepository.findByPublicIdInAndDeletedFalse(request.getAssignedEventIds())));
        }

        user = userRepository.save(user);
        
        String currentUserEmail = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        if (request.getAssignedEventIds() != null && !request.getAssignedEventIds().isEmpty()) {
            auditService.log(currentUserEmail, "EVENT_ASSIGNED", "USER", user.getId().toString(), "Assigned events to " + user.getUsername(), "SUCCESS");
            
            // Notify the user
            notificationService.send(
                    user.getId().toString(),
                    "USER",
                    "Event Assigned",
                    "You have been assigned to manage new events. Please check your assigned events.",
                    Channel.IN_APP,
                    user.getId().toString(),
                    "USER"
            );
        }
        return userMapper.toResponse(user);
    }

    @Override
    public void deleteUser(Long id) {
        // TODO: Implement soft-delete and cascade checks
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        checkSuperAdminPrivileges(user);
        user.setDeleted(true);
        userRepository.save(user);
    }

    @Override
    public void enableUser(Long id) {
        // TODO: Implement enable user logic
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        checkSuperAdminPrivileges(user);
        user.setEnabled(true);
        userRepository.save(user);
    }

    @Override
    public void disableUser(Long id) {
        // TODO: Implement disable user logic
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        checkSuperAdminPrivileges(user);
        user.setEnabled(false);
        userRepository.save(user);
    }

    @Override
    public void assignRoles(Long userId, Set<Long> roleIds) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        checkSuperAdminPrivileges(user);
        Set<Role> rolesToAdd = new java.util.HashSet<>(roleRepository.findAllById(roleIds));
        user.getRoles().addAll(rolesToAdd);
        userRepository.save(user);
    }

    @Override
    public void removeRoles(Long userId, Set<Long> roleIds) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        checkSuperAdminPrivileges(user);
        user.getRoles().removeIf(role -> roleIds.contains(role.getId()));
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserResponse> searchUsers(String query, Pageable pageable) {
        Page<UserResponse> page = userRepository.searchUsers(query, pageable)
                .map(userMapper::toResponse);
        return PageResponse.of(page);
    }

    /**
     * Prevents an ADMIN from modifying another ADMIN or SUPER_ADMIN account.
     */
    private void checkSuperAdminPrivileges(User targetUser) {
        Long currentUserId = com.imperium.ims.util.SecurityUtils.getCurrentUserId();
        if (currentUserId != null && currentUserId.equals(targetUser.getId())) {
            return; // User can modify their own profile
        }

        boolean isTargetAdmin = targetUser.getRoles().stream()
                .anyMatch(r -> r.getName().equals("ADMIN") || r.getName().equals("SUPER_ADMIN"));

        if (isTargetAdmin) {
            boolean isCurrentUserSuperAdmin = com.imperium.ims.util.SecurityUtils.hasRole("SUPER_ADMIN");
            if (!isCurrentUserSuperAdmin) {
                throw new org.springframework.security.access.AccessDeniedException(
                        "Only SUPER_ADMIN can modify other ADMIN or SUPER_ADMIN accounts.");
            }
        }
    }
}
