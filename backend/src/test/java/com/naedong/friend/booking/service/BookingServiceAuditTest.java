package com.naedong.friend.booking.service;

import static com.naedong.friend.testsupport.TestFixtures.BOOKING_ID;
import static com.naedong.friend.testsupport.TestFixtures.COMPANION_ID;
import static com.naedong.friend.testsupport.TestFixtures.CUSTOMER_ID;
import static com.naedong.friend.testsupport.TestFixtures.MEETING_SPOT_ID;
import static com.naedong.friend.testsupport.TestFixtures.bookingWithStatus;
import static com.naedong.friend.testsupport.TestFixtures.requestedBooking;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.naedong.friend.booking.domain.Booking;
import com.naedong.friend.booking.domain.BookingCheckin;
import com.naedong.friend.booking.domain.BookingStatus;
import com.naedong.friend.booking.domain.CompanionCategory;
import com.naedong.friend.booking.repository.BookingCheckinRepository;
import com.naedong.friend.booking.repository.BookingRepository;
import com.naedong.friend.common.RequestMetadata;
import com.naedong.friend.safety.service.AuditLogService;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BookingServiceAuditTest {

    @Mock
    BookingPolicyService bookingPolicyService;

    @Mock
    BookingRepository bookingRepository;

    @Mock
    BookingCheckinRepository bookingCheckinRepository;

    @Mock
    AuditLogService auditLogService;

    BookingService bookingService;

    @BeforeEach
    void setUp() {
        BookingStateMachine stateMachine = new BookingStateMachine(bookingRepository, auditLogService);
        bookingService = new BookingService(
                bookingPolicyService,
                bookingRepository,
                bookingCheckinRepository,
                stateMachine,
                auditLogService
        );
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(bookingCheckinRepository.save(any(BookingCheckin.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void createBookingCreatesAuditLog() {
        CreateBookingCommand command = validCreateBookingCommand();
        when(bookingPolicyService.validateBookingCreation(command)).thenReturn(new ValidatedBookingCreation(
                CUSTOMER_ID,
                COMPANION_ID,
                CompanionCategory.COFFEE_BUDDY,
                MEETING_SPOT_ID,
                command.startTime(),
                command.endTime()
        ));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking booking = invocation.getArgument(0);
            booking.setId(BOOKING_ID);
            return booking;
        });

        bookingService.createBooking(command, RequestMetadata.empty());

        verify(auditLogService).record(
                eq(CUSTOMER_ID),
                eq("BOOKING_REQUESTED"),
                eq("BOOKING"),
                eq(BOOKING_ID),
                eq("Customer requested booking"),
                eq(RequestMetadata.empty())
        );
    }

    @Test
    void acceptCreatesAuditLog() {
        Booking booking = requestedBooking();
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));

        bookingService.acceptBooking(BOOKING_ID, COMPANION_ID, RequestMetadata.empty());

        verify(auditLogService).record(
                eq(COMPANION_ID),
                eq("BOOKING_STATUS_CHANGED"),
                eq("BOOKING"),
                eq(BOOKING_ID),
                eq("REQUESTED -> ACCEPTED: Companion accepted booking"),
                eq(RequestMetadata.empty())
        );
    }

    @Test
    void checkInCreatesAuditLog() {
        Booking booking = bookingWithStatus(BookingStatus.ACCEPTED);
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));

        bookingService.checkIn(
                BOOKING_ID,
                CUSTOMER_ID,
                new BigDecimal("37.566500"),
                new BigDecimal("126.978000"),
                RequestMetadata.empty()
        );

        verify(auditLogService).record(
                eq(CUSTOMER_ID),
                eq("BOOKING_CHECK_IN"),
                eq("BOOKING"),
                eq(BOOKING_ID),
                eq("Participant checked in"),
                eq(RequestMetadata.empty())
        );
    }

    @Test
    void checkOutCreatesAuditLog() {
        Booking booking = bookingWithStatus(BookingStatus.IN_PROGRESS);
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));

        bookingService.checkOut(
                BOOKING_ID,
                CUSTOMER_ID,
                new BigDecimal("37.566500"),
                new BigDecimal("126.978000"),
                RequestMetadata.empty()
        );

        verify(auditLogService).record(
                eq(CUSTOMER_ID),
                eq("BOOKING_CHECK_OUT"),
                eq("BOOKING"),
                eq(BOOKING_ID),
                eq("Participant checked out"),
                eq(RequestMetadata.empty())
        );
    }

    private CreateBookingCommand validCreateBookingCommand() {
        return new CreateBookingCommand(
                CUSTOMER_ID,
                COMPANION_ID,
                "COFFEE_BUDDY",
                MEETING_SPOT_ID,
                Instant.parse("2026-07-02T10:00:00Z"),
                Instant.parse("2026-07-02T11:00:00Z")
        );
    }
}
