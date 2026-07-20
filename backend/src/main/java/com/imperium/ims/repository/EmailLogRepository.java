package com.imperium.ims.repository;

import com.imperium.ims.entity.EmailLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for {@link EmailLog} persistence.
 */
@Repository
public interface EmailLogRepository extends JpaRepository<EmailLog, Long> {

    Page<EmailLog> findByToAddress(String toAddress, Pageable pageable);
    
    List<EmailLog> findByToAddress(String toAddress);

    List<EmailLog> findByReferenceIdAndReferenceType(String referenceId, String referenceType);

    long countByStatus(String status);
}
