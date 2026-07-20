package com.imperium.ims.repository;

import com.imperium.ims.common.enums.Status;
import com.imperium.ims.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for {@link User} persistence operations.
 *
 * <p>Extends {@link JpaSpecificationExecutor} to support dynamic, specification-based
 * filtering (e.g. admin search by name, email, or status).
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>,
        JpaSpecificationExecutor<User> {

    Optional<User> findByPublicId(java.util.UUID publicId);
    
    java.util.List<User> findByPublicIdInAndDeletedFalse(java.util.Collection<java.util.UUID> publicIds);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsernameOrEmail(String username, String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    Page<User> findByStatus(Status status, Pageable pageable);

    @Query("SELECT u FROM User u WHERE " +
           "(LOWER(u.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           " LOWER(u.lastName)  LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           " LOWER(u.email)     LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "AND u.deleted = false")
    Page<User> searchUsers(@Param("query") String query, Pageable pageable);
    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.name IN :roleNames AND u.status = :status AND u.deleted = false")
    long countByRolesNameInAndStatus(@Param("roleNames") java.util.List<String> roleNames, @Param("status") Status status);

    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.name = :roleName AND u.deleted = false")
    long countByRolesName(@Param("roleName") String roleName);
}
