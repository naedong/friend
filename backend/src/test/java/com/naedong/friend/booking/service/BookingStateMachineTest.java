package com.naedong.friend.booking.service;

import static com.naedong.friend.testsupport.TestFixtures.COMPANION_ID;
import static com.naedong.friend.testsupport.TestFixtures.requestedBooking;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.naedong.friend.booking.domain.Booking;
import com.naedong.friend.booking.domain.BookingStatus;
import com.naedong.friend.booking.repository.BookingRepository;
import com.naedong.friend.common.RequestMetadata;
import com.naedong.friend.safety.service.AuditLogService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookingStateMachineTest {

    @Mock
    BookingRepository bookingRepository;

    @Mock
    AuditLogService auditLogService;

    @Test
    void invalidBookingStateTransitionIsRejected() {
        BookingStateMachine stateMachine = new BookingStateMachine(bookingRepository, auditLogService);
        Booking booking = requestedBooking();

        assertThatThrownBy(() -> stateMachine.transition(
                booking,
                BookingStatus.COMPLETED,
                COMPANION_ID,
                "Invalid direct completion",
                RequestMetadata.empty()
        ))
                .isInstanceOf(InvalidBookingStateTransitionException.class)
                .hasMessageContaining("REQUESTED -> COMPLETED");

        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void validTransitionCreatesAuditLog() {
        BookingStateMachine stateMachine = new BookingStateMachine(bookingRepository, auditLogService);
        Booking booking = requestedBooking();
        when(bookingRepository.save(booking)).thenReturn(booking);

        stateMachine.transition(booking, BookingStatus.ACCEPTED, COMPANION_ID, "Accepted", RequestMetadata.empty());

        verify(auditLogService).record(
                COMPANION_ID,
                "BOOKING_STATUS_CHANGED",
                "BOOKING",
                booking.getId(),
                "REQUESTED -> ACCEPTED: Accepted",
                RequestMetadata.empty()
        );
    }
}
