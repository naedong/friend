package com.naedong.friend.booking.service;

import static com.naedong.friend.testsupport.TestFixtures.COMPANION_ID;
import static com.naedong.friend.testsupport.TestFixtures.CUSTOMER_ID;
import static com.naedong.friend.testsupport.TestFixtures.approvedProfile;
import static com.naedong.friend.testsupport.TestFixtures.companion;
import static com.naedong.friend.testsupport.TestFixtures.customer;
import static com.naedong.friend.testsupport.TestFixtures.safeMeetingSpot;
import static com.naedong.friend.testsupport.TestFixtures.verifiedCompanion;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import com.naedong.friend.booking.api.BookingOptionsResponse;
import com.naedong.friend.booking.domain.SafeMeetingSpot;
import com.naedong.friend.booking.repository.SafeMeetingSpotRepository;
import com.naedong.friend.common.PolicyViolationException;
import com.naedong.friend.report.repository.BlockRepository;
import com.naedong.friend.user.domain.CompanionProfileStatus;
import com.naedong.friend.user.domain.User;
import com.naedong.friend.user.domain.UserStatus;
import com.naedong.friend.user.domain.UserVerification;
import com.naedong.friend.user.domain.VerificationStatus;
import com.naedong.friend.user.repository.CompanionProfileRepository;
import com.naedong.friend.user.repository.UserRepository;
import com.naedong.friend.user.repository.UserVerificationRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookingOptionsServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    UserVerificationRepository verificationRepository;

    @Mock
    CompanionProfileRepository companionProfileRepository;

    @Mock
    SafeMeetingSpotRepository meetingSpotRepository;

    @Mock
    BlockRepository blockRepository;

    BookingOptionsService service;

    @BeforeEach
    void setUp() {
        service = new BookingOptionsService(
                userRepository,
                verificationRepository,
                companionProfileRepository,
                meetingSpotRepository,
                blockRepository
        );
        lenient().when(userRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customer()));
        lenient().when(userRepository.findById(COMPANION_ID)).thenReturn(Optional.of(companion()));
        lenient().when(verificationRepository.findByUserId(COMPANION_ID))
                .thenReturn(Optional.of(verifiedCompanion()));
        lenient().when(companionProfileRepository.findAllByStatus(CompanionProfileStatus.APPROVED))
                .thenReturn(List.of(approvedProfile()));
        lenient().when(meetingSpotRepository.findAllByActiveTrueOrderByNameAsc())
                .thenReturn(List.of(safeMeetingSpot()));
    }

    @Test
    void returnsOnlyPublicEligibleBookingOptions() {
        BookingOptionsResponse response = service.getOptions(CUSTOMER_ID);

        assertThat(response.companions()).singleElement().satisfies(option -> {
            assertThat(option.id()).isEqualTo(COMPANION_ID);
            assertThat(option.displayName()).isEqualTo("Companion One");
            assertThat(option.bio()).doesNotContain("@", "+10000000002");
            assertThat(option.identityVerified()).isTrue();
            assertThat(option.livenessVerified()).isTrue();
        });
        assertThat(response.meetingSpots()).singleElement().satisfies(option -> {
            assertThat(option.name()).isEqualTo("Central Library Cafe");
            assertThat(option.address()).isEqualTo("1 Public Square");
        });
    }

    @Test
    void filtersBlockedCompanionsAndUnsafeMeetingSpots() {
        when(blockRepository.existsByBlockerIdAndBlockedUserId(CUSTOMER_ID, COMPANION_ID))
                .thenReturn(true);
        SafeMeetingSpot unsafeSpot = safeMeetingSpot();
        unsafeSpot.setPrivateSpace(true);
        when(meetingSpotRepository.findAllByActiveTrueOrderByNameAsc()).thenReturn(List.of(unsafeSpot));

        BookingOptionsResponse response = service.getOptions(CUSTOMER_ID);

        assertThat(response.companions()).isEmpty();
        assertThat(response.meetingSpots()).isEmpty();
    }

    @Test
    void filtersUnverifiedCompanions() {
        UserVerification verification = verifiedCompanion();
        verification.setIdentityStatus(VerificationStatus.PENDING);
        when(verificationRepository.findByUserId(COMPANION_ID)).thenReturn(Optional.of(verification));

        BookingOptionsResponse response = service.getOptions(CUSTOMER_ID);

        assertThat(response.companions()).isEmpty();
    }

    @Test
    void inactiveActorCannotBrowseBookingOptions() {
        User inactiveCustomer = customer();
        inactiveCustomer.setStatus(UserStatus.SUSPENDED);
        when(userRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(inactiveCustomer));

        assertThatThrownBy(() -> service.getOptions(CUSTOMER_ID))
                .isInstanceOf(PolicyViolationException.class)
                .hasMessageContaining("active users");
    }
}
