package com.imperium.ims.service;

import com.imperium.ims.dto.PageResponse;
import com.imperium.ims.common.enums.Channel;
import com.imperium.ims.entity.Notification;
import org.springframework.data.domain.Pageable;

/**
 * Service contract for notification dispatch and management.
 */
public interface NotificationService {

    /**
     * Sends a notification to a recipient via the specified channel.
     *
     * @param recipientId   user ID or guest email
     * @param recipientType USER or GUEST
     * @param title         notification title
     * @param message       notification body
     * @param channel       delivery channel
     * @param referenceId   optional reference (e.g. invitationId)
     * @param referenceType optional reference type (e.g. INVITATION)
     */
    void send(String recipientId, String recipientType, String title,
              String message, Channel channel,
              String referenceId, String referenceType);

    PageResponse<Notification> getNotificationsForUser(String recipientId, Pageable pageable);

    long getUnreadCount(String recipientId);

    void markAsRead(Long notificationId);

    void markAllAsRead(String recipientId);
}
