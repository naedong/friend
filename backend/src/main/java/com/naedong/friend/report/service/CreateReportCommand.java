package com.naedong.friend.report.service;

import com.naedong.friend.report.domain.ReportReason;
import java.util.UUID;

public record CreateReportCommand(
        UUID reporterId,
        UUID reportedUserId,
        UUID bookingId,
        ReportReason reason,
        boolean blockReportedUser
) {
}
