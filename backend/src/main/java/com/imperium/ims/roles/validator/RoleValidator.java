package com.imperium.ims.roles.validator;

import com.imperium.ims.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Custom validator for role-specific business rules.
 *
 * <p>This component is injected into the service layer to perform validations
 * that go beyond simple Bean Validation constraints — for example, checking
 * whether a role name is already taken before creating or renaming.
 */
@Component
@RequiredArgsConstructor
public class RoleValidator {

    private final RoleRepository roleRepository;

    /**
     * Validates that the given role name is not already in use.
     *
     * @param name the proposed role name
     * @throws com.imperium.ims.exception.DuplicateResourceException if taken
     */
    public void validateUniqueRoleName(String name) {
        // TODO: Implement validation logic
    }

    /**
     * Validates that a role is not a system role before allowing deletion.
     *
     * @param roleId the role to check
     * @throws com.imperium.ims.exception.BusinessException if it is a system role
     */
    public void validateNotSystemRole(Long roleId) {
        // TODO: Implement validation logic
    }
}
