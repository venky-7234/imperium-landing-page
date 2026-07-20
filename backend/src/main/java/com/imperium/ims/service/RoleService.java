package com.imperium.ims.service;

import com.imperium.ims.dto.RoleRequest;
import com.imperium.ims.dto.RoleResponse;

import java.util.List;

/**
 * Service contract for role management operations.
 */
public interface RoleService {

    /**
     * Retrieves all non-deleted roles.
     *
     * @return list of role responses
     */
    List<RoleResponse> getAllRoles();

    /**
     * Retrieves a single role by its ID.
     *
     * @param id the role identifier
     * @return the matching role response
     */
    RoleResponse getRoleById(Long id);

    /**
     * Creates a new role.
     *
     * @param request the role creation request
     * @return the created role response
     */
    RoleResponse createRole(RoleRequest request);

    /**
     * Updates an existing role.
     *
     * @param id      the role to update
     * @param request the update request
     * @return the updated role response
     */
    RoleResponse updateRole(Long id, RoleRequest request);

    /**
     * Soft-deletes a role by ID.
     *
     * @param id the role to delete
     */
    void deleteRole(Long id);
}
