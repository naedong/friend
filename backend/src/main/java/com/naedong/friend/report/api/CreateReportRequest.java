package com.naedong.friend.report.api;

import com.naedong.friend.report.domain.ReportReason;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateReportRequest(
        @NotNull UUID reportedUserId,
        @NotNull ReportReason reason,
        boolean blockReportedUser
) {
}
