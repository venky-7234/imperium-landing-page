package com.imperium.ims.service.impl;

import com.imperium.ims.entity.AuditLog;
import com.imperium.ims.repository.AuditLogRepository;
import com.imperium.ims.dto.PageResponse;
import com.imperium.ims.exception.ResourceNotFoundException;
import com.imperium.ims.entity.Role;
import com.imperium.ims.repository.RoleRepository;
import com.imperium.ims.dto.CreateUserRequest;
import com.imperium.ims.dto.UpdateUserRequest;
import com.imperium.ims.dto.UserResponse;
import com.imperium.ims.entity.User;
import com.imperium.ims.mapper.UserMapper;
import com.imperium.ims.repository.UserRepository;
import com.imperium.ims.service.SuperAdminService;
import com.imperium.ims.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SuperAdminServiceImpl implements SuperAdminService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final AuditLogRepository auditLogRepository;

    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
    private final com.imperium.ims.repository.RefreshTokenRepository refreshTokenRepository;
    private final com.imperium.ims.repository.LoginHistoryRepository loginHistoryRepository;
    private final com.imperium.ims.service.AuditService auditService;

    @Override
    public UserResponse createAdmin(CreateUserRequest request) {
        return userService.createAdmin(request);
    }

    @Override
    public UserResponse createUser(CreateUserRequest request) {
        return userService.createUser(request);
    }

    @Override
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        userMapper.updateEntity(request, user);
        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    public void activateAccount(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        user.setEnabled(true);
        userRepository.save(user);
        auditService.log("SUPER_ADMIN", "ACCOUNT_ACTIVATED", "USER", user.getId().toString(), "Account activated", "SUCCESS");
    }

    @Override
    public void deactivateAccount(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        user.setEnabled(false);
        userRepository.save(user);
        auditService.log("SUPER_ADMIN", "ACCOUNT_DEACTIVATED", "USER", user.getId().toString(), "Account deactivated", "SUCCESS");
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        user.setDeleted(true);
        userRepository.save(user);
        auditService.log("SUPER_ADMIN", "ACCOUNT_DELETED", "USER", user.getId().toString(), "Account deleted", "SUCCESS");
    }

    @Override
    public void assignRoles(Long id, Set<Long> roleIds) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        
        Set<Role> roles = new HashSet<>(roleRepository.findAllById(roleIds));
        user.setRoles(roles);
        userRepository.save(user);
        auditService.log("SUPER_ADMIN", "ROLES_ASSIGNED", "USER", user.getId().toString(), "Roles assigned", "SUCCESS");
    }

    @Override
    public void blockAccount(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        user.setStatus(com.imperium.ims.common.enums.Status.BLOCKED);
        userRepository.save(user);
        auditService.log("SUPER_ADMIN", "ACCOUNT_BLOCKED", "USER", user.getId().toString(), "Account blocked", "SUCCESS");
    }

    @Override
    public void unblockAccount(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        user.setStatus(com.imperium.ims.common.enums.Status.ACTIVE);
        userRepository.save(user);
        auditService.log("SUPER_ADMIN", "ACCOUNT_UNBLOCKED", "USER", user.getId().toString(), "Account unblocked", "SUCCESS");
    }


    @Transactional(readOnly = true)
    @Override
    public PageResponse<com.imperium.ims.entity.LoginHistory> getUserLoginHistory(Long userId, Pageable pageable) {
        Page<com.imperium.ims.entity.LoginHistory> page = loginHistoryRepository.findByUserIdOrderByLoginTimeDesc(userId, pageable);
        return PageResponse.of(page);
    }

    @Transactional(readOnly = true)
    @Override
    public PageResponse<AuditLog> getAdminActivities(Pageable pageable) {
        Page<AuditLog> page = auditLogRepository.findAdminActivities(pageable);
        return PageResponse.of(page);
    }
}
