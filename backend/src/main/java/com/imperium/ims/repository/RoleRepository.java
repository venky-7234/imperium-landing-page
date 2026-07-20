package com.imperium.ims.repository;

import com.imperium.ims.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link Role} persistence operations.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);

    boolean existsByName(String name);

    List<Role> findBySystemRoleTrue();

    List<Role> findByDeletedFalse();
}
