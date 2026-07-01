package com.naedong.friend.report.service;

import com.naedong.friend.booking.domain.Booking;
import com.naedong.friend.booking.domain.BookingStatus;
import com.naedong.friend.booking.repository.BookingRepository;
import com.naedong.friend.booking.service.BookingStateMachine;
import com.naedong.friend.common.DomainNotFoundException;
import com.naedong.friend.common.PolicyViolationException;
import com.naedong.friend.common.RequestMetadata;
import com.naedong.friend.gateway.PaymentGateway;
import com.naedong.friend.report.domain.Block;
import com.naedong.friend.report.domain.Report;
import com.naedong.friend.report.domain.ReportReason;
import com.naedong.friend.report.domain.ReportStatus;
import com.naedong.friend.report.repository.BlockRepository;
import com.naedong.friend.report.repository.ReportRepository;
import com.naedong.friend.safety.domain.BookingSafetyEvent;
import com.naedong.friend.safety.domain.BookingSafetyEventType;
import com.naedong.friend.safety.domain.SafetyEventSeverity;
import com.naedong.friend.safety.repository.BookingSafetyEventRepository;
import com.naedong.friend.safety.service.AuditLogService;
import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportService {

    private static final Set<ReportStatus> ACTIVE_REPORT_STATUSES = EnumSet.of(
            ReportStatus.OPEN,
            ReportStatus.UNDER_REVIEW
    );

    private final ReportRepository reportRepository;
    private final BookingRepository bookingRepository;
    private final BlockRepository blockRepository;
    private final BookingStateMachine bookingStateMachine;
    private final AuditLogService auditLogService;
    private final BookingSafetyEventRepository safetyEventRepository;
    private final PaymentGateway paymentGateway;

    public ReportService(
            ReportRepository reportRepository,
            BookingRepository bookingRepository,
            BlockRepository blockRepository,
            BookingStateMachine bookingStateMachine,
            AuditLogService auditLogService,
            BookingSafetyEventRepository safetyEventRepository,
            PaymentGateway paymentGateway
    ) {
        this.reportRepository = reportRepository;
        this.bookingRepository = bookingRepository;
        this.blockRepository = blockRepository;
        this.bookingStateMachine = bookingStateMachine;
        this.auditLogService = auditLogService;
        this.safetyEventRepository = safetyEventRepository;
        this.paymentGateway = paymentGateway;
    }

    @Transactional
    public Report createReport(CreateReportCommand command, RequestMetadata metadata) {
        if (command.reporterId() == null) {
            throw new PolicyViolationException("Anonymous users cannot create reports.");
        }
        Booking booking = bookingRepository.findById(command.bookingId())
                .orElseThrow(() -> new DomainNotFoundException("Booking not found: " + command.bookingId()));
        validateReporterAndReportedUser(command, booking);

        boolean duplicateActiveReport = reportRepository.existsByReporterIdAndBookingIdAndStatusIn(
                command.reporterId(),
                command.bookingId(),
                ACTIVE_REPORT_STATUSES
        );
        if (duplicateActiveReport) {
            throw new PolicyViolationException("An active report for this booking already exists from this reporter.");
        }

        Report report = new Report();
        report.setReporterId(command.reporterId());
        report.setReportedUserId(command.reportedUserId());
        report.setBookingId(command.bookingId());
        report.setReason(command.reason());
        report.setStatus(ReportStatus.OPEN);
        Report savedReport = reportRepository.save(report);

        auditLogService.record(
                command.reporterId(),
                "REPORT_CREATED",
                "REPORT",
                savedReport.getId(),
                "Report reason: " + command.reason(),
                metadata
        );

        if (command.blockReportedUser()
                && !blockRepository.existsByBlockerIdAndBlockedUserId(command.reporterId(), command.reportedUserId())) {
            Block block = new Block();
            block.setBlockerId(command.reporterId());
            block.setBlockedUserId(command.reportedUserId());
            blockRepository.save(block);
            auditLogService.record(command.reporterId(), "USER_BLOCKED", "USER", command.reportedUserId(), "Block requested with report", metadata);
        }

        moveBookingToSafetyState(booking, command.reporterId(), command.reason(), metadata);
        return savedReport;
    }

    private void validateReporterAndReportedUser(CreateReportCommand command, Booking booking) {
        boolean reporterIsParticipant = booking.getCustomerId().equals(command.reporterId())
                || booking.getCompanionId().equals(command.reporterId());
        boolean reportedIsParticipant = booking.getCustomerId().equals(command.reportedUserId())
                || booking.getCompanionId().equals(command.reportedUserId());
        if (!reporterIsParticipant || !reportedIsParticipant || command.reporterId().equals(command.reportedUserId())) {
            throw new PolicyViolationException("Reports must be between participants on the booking.");
        }
    }

    private void moveBookingToSafetyState(
            Booking booking,
            UUID actorUserId,
            ReportReason reason,
            RequestMetadata metadata
    ) {
        Booking current = booking;
        if (isReportableActiveStatus(current.getStatus())) {
            current = bookingStateMachine.transition(current, BookingStatus.REPORTED, actorUserId, "Booking reported", metadata);
        }
        if (current.getStatus() == BookingStatus.REPORTED) {
            current = bookingStateMachine.transition(current, BookingStatus.SAFETY_HOLD, actorUserId, "Safety review required after report", metadata);
        }
        if (reason == ReportReason.OFF_PLATFORM_PAYMENT && current.getStatus() == BookingStatus.SAFETY_HOLD) {
            createSafetyEvent(current.getId(), BookingSafetyEventType.PAYOUT_FROZEN, SafetyEventSeverity.HIGH, "Payout frozen after off-platform payment report.");
            paymentGateway.freezePayout(current.getId(), "Off-platform payment report");
            bookingStateMachine.transition(current, BookingStatus.PAYOUT_HOLD, actorUserId, "Payout frozen after report", metadata);
        }
    }

    private boolean isReportableActiveStatus(BookingStatus status) {
        return status == BookingStatus.REQUESTED
                || status == BookingStatus.ACCEPTED
                || status == BookingStatus.CHECKED_IN
                || status == BookingStatus.IN_PROGRESS
                || status == BookingStatus.CHECKOUT_PENDING;
    }

    private void createSafetyEvent(
            UUID bookingId,
            BookingSafetyEventType type,
            SafetyEventSeverity severity,
            String message
    ) {
        BookingSafetyEvent event = new BookingSafetyEvent();
        event.setBookingId(bookingId);
        event.setType(type);
        event.setSeverity(severity);
        event.setMessage(message);
        safetyEventRepository.save(event);
    }
}
