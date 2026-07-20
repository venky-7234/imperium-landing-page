package com.imperium.ims.service.impl;

import com.imperium.ims.dto.EventAnalyticsResponse;
import com.imperium.ims.service.AnalyticsService;
import com.imperium.ims.entity.InvitationStatus;
import com.imperium.ims.repository.InvitationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of {@link AnalyticsService}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalyticsServiceImpl implements AnalyticsService {

    private final InvitationRepository invitationRepository;

    @Override
    public EventAnalyticsResponse getEventAnalytics(Long eventId) {
        // TODO: Join with events table, compute rates, add channel breakdown
        List<Object[]> rows = invitationRepository.countByEventIdGroupByStatus(eventId);
        Map<String, Long> breakdown = new HashMap<>();
        for (Object[] row : rows) {
            breakdown.put(((InvitationStatus) row[0]).name(), (Long) row[1]);
        }

        long sent      = breakdown.getOrDefault("SENT", 0L);
        long confirmed = breakdown.getOrDefault("CONFIRMED", 0L);
        long declined  = breakdown.getOrDefault("DECLINED", 0L);
        long tentative = breakdown.getOrDefault("TENTATIVE", 0L);
        long responded = confirmed + declined + tentative;

        double responseRate  = sent > 0 ? (double) responded / sent * 100 : 0;
        double acceptanceRate = (confirmed + declined) > 0
                ? (double) confirmed / (confirmed + declined) * 100 : 0;

        return EventAnalyticsResponse.builder()
                .eventId(eventId)
                .totalInvitations(invitationRepository.countByEventId(eventId))
                .sent(sent)
                .confirmed(confirmed)
                .declined(declined)
                .tentative(tentative)
                .viewed(breakdown.getOrDefault("VIEWED", 0L))
                .expired(breakdown.getOrDefault("EXPIRED", 0L))
                .cancelled(breakdown.getOrDefault("CANCELLED", 0L))
                .responseRate(responseRate)
                .acceptanceRate(acceptanceRate)
                .statusBreakdown(breakdown)
                .build();
    }

    @Override
    public EventAnalyticsResponse getOrganizerDashboard(Long organizerId) {
        // TODO: Aggregate across all events for the organizer
        return EventAnalyticsResponse.builder().build();
    }
}
