package com.imperium.ims.service.impl;

import com.imperium.ims.exception.DuplicateResourceException;
import com.imperium.ims.exception.ResourceNotFoundException;
import com.imperium.ims.dto.RoleRequest;
import com.imperium.ims.dto.RoleResponse;
import com.imperium.ims.entity.Role;
import com.imperium.ims.mapper.RoleMapper;
import com.imperium.ims.repository.RoleRepository;
import com.imperium.ims.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of {@link RoleService}.
 *
 * <p>Business logic is intentionally omitted in this architectural skeleton.
 * All methods contain the structural contract and will be filled in during
 * the implementation phase.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponse> getAllRoles() {

        return roleRepository.findByDeletedFalse().stream()
                .map(roleMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RoleResponse getRoleById(Long id) {

        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", id));
        return roleMapper.toResponse(role);
    }

    @Override
    public RoleResponse createRole(RoleRequest request) {

        if (roleRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Role", "name", request.getName());
        }
        Role role = roleMapper.toEntity(request);
        return roleMapper.toResponse(roleRepository.save(role));
    }

    @Override
    public RoleResponse updateRole(Long id, RoleRequest request) {

        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", id));
        roleMapper.updateEntity(request, role);
        return roleMapper.toResponse(roleRepository.save(role));
    }

    @Override
    public void deleteRole(Long id) {

        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", id));
        role.setDeleted(true);
        roleRepository.save(role);
    }
}
