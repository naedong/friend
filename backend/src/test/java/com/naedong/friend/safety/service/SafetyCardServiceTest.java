package com.naedong.friend.safety.service;

import static com.naedong.friend.testsupport.TestFixtures.BOOKING_ID;
import static com.naedong.friend.testsupport.TestFixtures.COMPANION_ID;
import static com.naedong.friend.testsupport.TestFixtures.CUSTOMER_ID;
import static com.naedong.friend.testsupport.TestFixtures.MEETING_SPOT_ID;
import static com.naedong.friend.testsupport.TestFixtures.activeSafetyCard;
import static com.naedong.friend.testsupport.TestFixtures.bookingWithStatus;
import static com.naedong.friend.testsupport.TestFixtures.companion;
import static com.naedong.friend.testsupport.TestFixtures.customer;
import static com.naedong.friend.testsupport.TestFixtures.safeMeetingSpot;
import static com.naedong.friend.testsupport.TestFixtures.verifiedCompanion;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.naedong.friend.booking.domain.BookingStatus;
import com.naedong.friend.booking.repository.BookingRepository;
import com.naedong.friend.booking.repository.SafeMeetingSpotRepository;
import com.naedong.friend.safety.repository.SafetyCardRepository;
import com.naedong.friend.user.repository.UserRepository;
import com.naedong.friend.user.repository.UserVerificationRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SafetyCardServiceTest {

    @Mock
    SafetyCardRepository safetyCardRepository;

    @Mock
    BookingRepository bookingRepository;

    @Mock
    SafeMeetingSpotRepository safeMeetingSpotRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    UserVerificationRepository verificationRepository;

    @Test
    void safetyCardDoesNotExposePrivateData() throws Exception {
        Clock clock = Clock.fixed(Instant.parse("2026-07-02T12:00:00Z"), ZoneOffset.UTC);
        SafetyCardService service = new SafetyCardService(
                safetyCardRepository,
                bookingRepository,
                safeMeetingSpotRepository,
                userRepository,
                verificationRepository,
                clock
        );
        when(safetyCardRepository.findByPublicToken("safe-token")).thenReturn(Optional.of(activeSafetyCard()));
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(bookingWithStatus(BookingStatus.IN_PROGRESS)));
        when(safeMeetingSpotRepository.findById(MEETING_SPOT_ID)).thenReturn(Optional.of(safeMeetingSpot()));
        when(userRepository.findById(COMPANION_ID)).thenReturn(Optional.of(companion()));
        when(userRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customer()));
        when(verificationRepository.findByUserId(COMPANION_ID)).thenReturn(Optional.of(verifiedCompanion()));

        SafetyCardView view = service.getSafetyCardView("safe-token");
        String json = new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(view);

        assertThat(json).contains("Companion One", "Customer One", "Central Library Cafe");
        assertThat(json).doesNotContain("companion@example.test", "customer@example.test", "+10000000001", "+10000000002");
        assertThat(json).doesNotContain("email", "phone");
    }
}
