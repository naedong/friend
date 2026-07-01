package com.naedong.friend.safety.service;

import static com.naedong.friend.testsupport.TestFixtures.BOOKING_ID;
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
import com.naedong.friend.gateway.NotificationGateway;
import com.naedong.friend.gateway.TrustedContactAlertGateway;
import com.naedong.friend.safety.domain.BookingSafetyEvent;
import com.naedong.friend.safety.domain.BookingSafetyEventType;
import com.naedong.friend.safety.repository.BookingSafetyEventRepository;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SafetyEscalationServiceTest {

    @Mock
    BookingRepository bookingRepository;

    @Mock
    BookingSafetyEventRepository safetyEventRepository;

    @Mock
    TrustedContactAlertGateway trustedContactAlertGateway;

    @Mock
    NotificationGateway notificationGateway;

    @Mock
    AuditLogService auditLogService;

    SafetyEscalationService service;

    @BeforeEach
    void setUp() {
        BookingStateMachine stateMachine = new BookingStateMachine(bookingRepository, auditLogService);
        service = new SafetyEscalationService(
                bookingRepository,
                stateMachine,
                safetyEventRepository,
                trustedContactAlertGateway,
                notificationGateway
        );
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(safetyEventRepository.save(any(BookingSafetyEvent.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void missedCheckoutCreatesSafetyEventsAndSafetyHold() {
        Booking booking = bookingWithStatus(BookingStatus.IN_PROGRESS);
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));

        var events = service.handleMissedCheckout(BOOKING_ID, Instant.parse("2026-07-02T11:30:00Z"));

        assertThat(events).extracting(BookingSafetyEvent::getType)
                .containsExactly(
                        BookingSafetyEventType.CHECKOUT_MISSED,
                        BookingSafetyEventType.TRUSTED_CONTACT_ALERTED,
                        BookingSafetyEventType.MODERATOR_REVIEW_CREATED
                );
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.SAFETY_HOLD);
        verify(notificationGateway).notifyModerator(eq(BOOKING_ID), any(String.class));
    }
}
