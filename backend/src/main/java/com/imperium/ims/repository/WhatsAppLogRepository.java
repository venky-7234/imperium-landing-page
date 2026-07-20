package com.imperium.ims.repository;

import com.imperium.ims.entity.WhatsAppLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for {@link WhatsAppLog} persistence.
 */
@Repository
public interface WhatsAppLogRepository extends JpaRepository<WhatsAppLog, Long> {

    Optional<WhatsAppLog> findByProviderMessageId(String providerMessageId);

    long countByStatus(String status);

    java.util.List<WhatsAppLog> findByToNumber(String toNumber);
}
