package com.imperium.ims.repository;

import com.imperium.ims.entity.WhatsAppQueue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WhatsAppQueueRepository extends JpaRepository<WhatsAppQueue, Long> {
}
