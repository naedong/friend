package com.naedong.friend.report.repository;

import com.naedong.friend.report.domain.Report;
import com.naedong.friend.report.domain.ReportStatus;
import java.util.Collection;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, UUID> {

    boolean existsByReporterIdAndBookingIdAndStatusIn(
            UUID reporterId,
            UUID bookingId,
            Collection<ReportStatus> statuses
    );
}
