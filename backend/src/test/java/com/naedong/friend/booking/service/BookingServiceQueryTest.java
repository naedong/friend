package com.naedong.friend.booking.service;

import static com.naedong.friend.testsupport.TestFixtures.CUSTOMER_ID;
import static com.naedong.friend.testsupport.TestFixtures.requestedBooking;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.naedong.friend.booking.domain.Booking;
import com.naedong.friend.booking.repository.BookingCheckinRepository;
import com.naedong.friend.booking.repository.BookingRepository;
import com.naedong.friend.safety.service.AuditLogService;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class BookingServiceQueryTest {

    @Test
    void returnsLatestBookingForEitherParticipantRole() {
        BookingRepository bookingRepository = mock(BookingRepository.class);
        Booking booking = requestedBooking();
        when(bookingRepository.findFirstByCustomerIdOrCompanionIdOrderByCreatedAtDesc(
                CUSTOMER_ID,
                CUSTOMER_ID
        )).thenReturn(Optional.of(booking));
        BookingService service = new BookingService(
                mock(BookingPolicyService.class),
                bookingRepository,
                mock(BookingCheckinRepository.class),
                mock(BookingStateMachine.class),
                mock(AuditLogService.class)
        );

        Optional<Booking> result = service.findLatestBookingForParticipant(CUSTOMER_ID);

        assertThat(result).containsSame(booking);
    }
}
