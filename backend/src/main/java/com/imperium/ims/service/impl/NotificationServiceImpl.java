package com.imperium.ims.service.impl;

import com.imperium.ims.dto.PageResponse;
import com.imperium.ims.common.enums.Channel;
import com.imperium.ims.exception.ResourceNotFoundException;
import com.imperium.ims.entity.Notification;
import com.imperium.ims.repository.NotificationRepository;
import com.imperium.ims.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of {@link NotificationService}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Async("asyncTaskExecutor")
    @Override
    public void send(String recipientId, String recipientType, String title,
                     String message, Channel channel,
                     String referenceId, String referenceType) {
        // TODO: Dispatch via email/WhatsApp/in-app based on channel, persist Notification
        Notification notification = Notification.builder()
                .recipientId(recipientId)
                .recipientType(recipientType)
                .title(title)
                .message(message)
                .channel(channel)
                .referenceId(referenceId)
                .referenceType(referenceType)
                .build();
        notificationRepository.save(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<Notification> getNotificationsForUser(String recipientId, Pageable pageable) {
        return PageResponse.of(
                notificationRepository.findByRecipientIdOrderByCreatedAtDesc(recipientId, pageable));
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(String recipientId) {
        return notificationRepository.countByRecipientIdAndReadFalse(recipientId);
    }

    @Override
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Override
    public void markAllAsRead(String recipientId) {
        notificationRepository.markAllAsRead(recipientId);
    }
}
