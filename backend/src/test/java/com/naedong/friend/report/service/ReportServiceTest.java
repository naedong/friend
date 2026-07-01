package com.naedong.friend.report.service;

import static com.naedong.friend.testsupport.TestFixtures.BOOKING_ID;
import static com.naedong.friend.testsupport.TestFixtures.COMPANION_ID;
import static com.naedong.friend.testsupport.TestFixtures.CUSTOMER_ID;
import static com.naedong.friend.testsupport.TestFixtures.REPORT_ID;
import static com.naedong.friend.testsupport.TestFixtures.bookingWithStatus;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.naedong.friend.booking.domain.Booking;
import com.naedong.friend.booking.domain.BookingStatus;
import com.naedong.friend.booking.repository.BookingRepository;
import com.naedong.friend.booking.service.BookingStateMachine;
import com.naedong.friend.common.RequestMetadata;
import com.naedong.friend.gateway.PaymentGateway;
import com.naedong.friend.report.domain.Block;
import com.naedong.friend.report.domain.Report;
import com.naedong.friend.report.domain.ReportReason;
import com.naedong.friend.report.domain.ReportStatus;
import com.naedong.friend.report.repository.BlockRepository;
import com.naedong.friend.report.repository.ReportRepository;
import com.naedong.friend.safety.domain.BookingSafetyEvent;
import com.naedong.friend.safety.repository.BookingSafetyEventRepository;
import com.naedong.friend.safety.service.AuditLogService;
import java.util.Collection;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ReportServiceTest {

    @Mock
    ReportRepository reportRepository;

    @Mock
    BookingRepository bookingRepository;

    @Mock
    BlockRepository blockRepository;

    @Mock
    AuditLogService auditLogService;

    @Mock
    BookingSafetyEventRepository safetyEventRepository;

    @Mock
    PaymentGateway paymentGateway;

    ReportService reportService;

    @BeforeEach
    void setUp() {
        BookingStateMachine stateMachine = new BookingStateMachine(bookingRepository, auditLogService);
        reportService = new ReportService(
                reportRepository,
                bookingRepository,
                blockRepository,
                stateMachine,
                auditLogService,
                safetyEventRepository,
                paymentGateway
        );
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(reportRepository.save(any(Report.class))).thenAnswer(invocation -> {
            Report report = invocation.getArgument(0);
            report.setId(REPORT_ID);
            return report;
        });
        when(safetyEventRepository.save(any(BookingSafetyEvent.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(blockRepository.save(any(Block.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void reportCreatesSafetyHold() {
        Booking booking = bookingWithStatus(BookingStatus.IN_PROGRESS);
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));
        when(reportRepository.existsByReporterIdAndBookingIdAndStatusIn(
                eq(CUSTOMER_ID),
                eq(BOOKING_ID),
                ArgumentMatchers.<Collection<ReportStatus>>any()
        ))
                .thenReturn(false);

        reportService.createReport(new CreateReportCommand(
                CUSTOMER_ID,
                COMPANION_ID,
                BOOKING_ID,
                ReportReason.HARASSMENT,
                false
        ), RequestMetadata.empty());

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.SAFETY_HOLD);
        verify(auditLogService).record(
                eq(CUSTOMER_ID),
                eq("REPORT_CREATED"),
                eq("REPORT"),
                eq(REPORT_ID),
                eq("Report reason: HARASSMENT"),
                eq(RequestMetadata.empty())
        );
    }

    @Test
    void offPlatformPaymentReportCreatesPayoutHold() {
        Booking booking = bookingWithStatus(BookingStatus.ACCEPTED);
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));
        when(reportRepository.existsByReporterIdAndBookingIdAndStatusIn(
                eq(CUSTOMER_ID),
                eq(BOOKING_ID),
                ArgumentMatchers.<Collection<ReportStatus>>any()
        ))
                .thenReturn(false);

        reportService.createReport(new CreateReportCommand(
                CUSTOMER_ID,
                COMPANION_ID,
                BOOKING_ID,
                ReportReason.OFF_PLATFORM_PAYMENT,
                false
        ), RequestMetadata.empty());

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.PAYOUT_HOLD);
        verify(paymentGateway).freezePayout(BOOKING_ID, "Off-platform payment report");
    }
}
