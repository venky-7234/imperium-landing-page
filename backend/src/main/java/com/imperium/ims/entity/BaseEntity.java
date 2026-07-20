package com.imperium.ims.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Base entity with JPA Auditing for all persistent entities in the system.
 *
 * <p>All module-level entities must extend this class to inherit:
 * <ul>
 *   <li>Auto-generated surrogate {@code id} (BIGINT, auto-increment)</li>
 *   <li>Audit timestamps ({@code createdAt}, {@code updatedAt})</li>
 *   <li>Audit users ({@code createdBy}, {@code updatedBy})</li>
 *   <li>Soft-delete flag ({@code deleted})</li>
 * </ul>
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @CreatedBy
    @Column(name = "created_by", updatable = false, length = 100)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    /**
     * Soft-delete flag.  Entities are never physically deleted; they are
     * marked as {@code deleted = true} and excluded via repository-level
     * {@code @Where} clauses.
     */
    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    @Version
    @Column(name = "version")
    private Long version;

    @Column(name = "public_id", unique = true, updatable = false)
    private java.util.UUID publicId;

    @PrePersist
    public void generatePublicId() {
        if (this.publicId == null) {
            this.publicId = java.util.UUID.randomUUID();
        }
    }
}
