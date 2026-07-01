package com.naedong.friend.report.api;

import com.naedong.friend.report.domain.Report;
import java.util.UUID;

public record ReportResponse(
        UUID id,
        UUID reporterId,
        UUID reportedUserId,
        UUID bookingId,
        String reason,
        String status
) {

    public static ReportResponse from(Report report) {
        return new ReportResponse(
                report.getId(),
                report.getReporterId(),
                report.getReportedUserId(),
                report.getBookingId(),
                report.getReason().name(),
                report.getStatus().name()
        );
    }
}
