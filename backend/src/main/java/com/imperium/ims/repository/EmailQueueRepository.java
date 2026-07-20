package com.imperium.ims.repository;

import com.imperium.ims.entity.EmailQueue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EmailQueueRepository extends JpaRepository<EmailQueue, Long> {
}
