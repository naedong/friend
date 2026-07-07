package com.naedong.friend.booking.service;

import static com.naedong.friend.testsupport.TestFixtures.BOOKING_ID;
import static com.naedong.friend.testsupport.TestFixtures.COMPANION_ID;
import static com.naedong.friend.testsupport.TestFixtures.CUSTOMER_ID;
import static com.naedong.friend.testsupport.TestFixtures.bookingWithStatus;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.naedong.friend.booking.domain.Booking;
import com.naedong.friend.booking.domain.BookingCheckin;
import com.naedong.friend.booking.domain.BookingCheckinType;
import com.naedong.friend.booking.domain.BookingStatus;
import com.naedong.friend.booking.repository.BookingCheckinRepository;
import com.naedong.friend.booking.repository.BookingRepository;
import com.naedong.friend.common.PolicyViolationException;
import com.naedong.friend.common.RequestMetadata;
import com.naedong.friend.safety.service.AuditLogService;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BookingServiceParticipantCheckinTest {

    @Mock
    BookingPolicyService bookingPolicyService;

    @Mock
    BookingRepository bookingRepository;

    @Mock
    BookingCheckinRepository bookingCheckinRepository;

    @Mock
    AuditLogService auditLogService;

    BookingService bookingService;
    Set<String> savedActions;

    @BeforeEach
    void setUp() {
        savedActions = new HashSet<>();
        BookingStateMachine stateMachine = new BookingStateMachine(bookingRepository, auditLogService);
        bookingService = new BookingService(
                bookingPolicyService,
                bookingRepository,
                bookingCheckinRepository,
                stateMachine,
                auditLogService
        );
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(bookingCheckinRepository.save(any(BookingCheckin.class))).thenAnswer(invocation -> {
            BookingCheckin checkin = invocation.getArgument(0);
            savedActions.add(key(checkin.getBookingId(), checkin.getUserId(), checkin.getType()));
            return checkin;
        });
        when(bookingCheckinRepository.existsByBookingIdAndUserIdAndType(any(), any(), any()))
                .thenAnswer(invocation -> savedActions.contains(key(
                        invocation.getArgument(0),
                        invocation.getArgument(1),
                        invocation.getArgument(2)
                )));
    }

    @Test
    void firstCheckInDoesNotFullyStartBooking() {
        Booking booking = bookingWithStatus(BookingStatus.ACCEPTED);
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));

        Booking result = bookingService.checkIn(BOOKING_ID, CUSTOMER_ID, latitude(), longitude(), RequestMetadata.empty());

        assertThat(result.getStatus()).isEqualTo(BookingStatus.CHECKED_IN);
    }

    @Test
    void bothCheckInsStartBooking() {
        Booking booking = bookingWithStatus(BookingStatus.ACCEPTED);
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));

        bookingService.checkIn(BOOKING_ID, CUSTOMER_ID, latitude(), longitude(), RequestMetadata.empty());
        Booking result = bookingService.checkIn(BOOKING_ID, COMPANION_ID, latitude(), longitude(), RequestMetadata.empty());

        assertThat(result.getStatus()).isEqualTo(BookingStatus.IN_PROGRESS);
    }

    @Test
    void firstCheckOutDoesNotCompleteBooking() {
        Booking booking = bookingWithStatus(BookingStatus.IN_PROGRESS);
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));

        Booking result = bookingService.checkOut(BOOKING_ID, CUSTOMER_ID, latitude(), longitude(), RequestMetadata.empty());

        assertThat(result.getStatus()).isEqualTo(BookingStatus.CHECKOUT_PENDING);
    }

    @Test
    void bothCheckOutsCompleteBooking() {
        Booking booking = bookingWithStatus(BookingStatus.IN_PROGRESS);
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));

        bookingService.checkOut(BOOKING_ID, CUSTOMER_ID, latitude(), longitude(), RequestMetadata.empty());
        Booking result = bookingService.checkOut(BOOKING_ID, COMPANION_ID, latitude(), longitude(), RequestMetadata.empty());

        assertThat(result.getStatus()).isEqualTo(BookingStatus.COMPLETED);
    }

    @Test
    void duplicateCheckInIsRejected() {
        Booking booking = bookingWithStatus(BookingStatus.ACCEPTED);
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));

        bookingService.checkIn(BOOKING_ID, CUSTOMER_ID, latitude(), longitude(), RequestMetadata.empty());

        assertThatThrownBy(() -> bookingService.checkIn(BOOKING_ID, CUSTOMER_ID, latitude(), longitude(), RequestMetadata.empty()))
                .isInstanceOf(PolicyViolationException.class)
                .hasMessageContaining("Duplicate");
    }

    @Test
    void duplicateCheckOutIsRejected() {
        Booking booking = bookingWithStatus(BookingStatus.IN_PROGRESS);
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));

        bookingService.checkOut(BOOKING_ID, CUSTOMER_ID, latitude(), longitude(), RequestMetadata.empty());

        assertThatThrownBy(() -> bookingService.checkOut(BOOKING_ID, CUSTOMER_ID, latitude(), longitude(), RequestMetadata.empty()))
                .isInstanceOf(PolicyViolationException.class)
                .hasMessageContaining("Duplicate");
    }

    private String key(UUID bookingId, UUID userId, BookingCheckinType type) {
        return bookingId + ":" + userId + ":" + type;
    }

    private BigDecimal latitude() {
        return new BigDecimal("37.566500");
    }

    private BigDecimal longitude() {
        return new BigDecimal("126.978000");
    }
}
