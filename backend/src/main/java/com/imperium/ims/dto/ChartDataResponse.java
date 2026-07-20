package com.imperium.ims.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChartDataResponse {
    
    private List<DailyMetric> applicationsReceived;
    private List<DailyMetric> applicationsApproved;
    private List<DailyMetric> applicationsRejected;
    private List<DailyMetric> invitationsSent;
    private List<TopMetric> topCompanies;
    private List<TopMetric> topCities;
    private InvitationStats invitationStats;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyMetric {
        private String date; // YYYY-MM-DD
        private long count;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopMetric {
        private String name;
        private long count;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InvitationStats {
        private long totalGenerated;
        private long totalSent;
        private long totalResponded;
        private long totalCancelled;
    }
}
