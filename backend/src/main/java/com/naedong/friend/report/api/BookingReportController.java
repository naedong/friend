package com.naedong.friend.report.api;

import com.naedong.friend.common.api.RequestMetadataFactory;
import com.naedong.friend.report.domain.Report;
import com.naedong.friend.report.service.CreateReportCommand;
import com.naedong.friend.report.service.ReportService;
import com.naedong.friend.security.ActorProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookings/{bookingId}/report")
public class BookingReportController {

    private final ReportService reportService;
    private final ActorProvider actorProvider;

    public BookingReportController(ReportService reportService, ActorProvider actorProvider) {
        this.reportService = reportService;
        this.actorProvider = actorProvider;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReportResponse createReport(
            @PathVariable UUID bookingId,
            @Valid @RequestBody CreateReportRequest body,
            HttpServletRequest request
    ) {
        // TODO AUTHZ: require authenticated booking participant before public beta.
        UUID actorUserId = actorProvider.currentActorId(request);
        Report report = reportService.createReport(
                new CreateReportCommand(
                        actorUserId,
                        body.reportedUserId(),
                        bookingId,
                        body.reason(),
                        body.blockReportedUser()
                ),
                RequestMetadataFactory.from(request)
        );
        return ReportResponse.from(report);
    }
}
