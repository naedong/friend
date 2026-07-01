package com.naedong.friend.booking.service;

import static com.naedong.friend.testsupport.TestFixtures.COMPANION_ID;
import static com.naedong.friend.testsupport.TestFixtures.CUSTOMER_ID;
import static com.naedong.friend.testsupport.TestFixtures.MEETING_SPOT_ID;
import static com.naedong.friend.testsupport.TestFixtures.approvedProfile;
import static com.naedong.friend.testsupport.TestFixtures.companion;
import static com.naedong.friend.testsupport.TestFixtures.customer;
import static com.naedong.friend.testsupport.TestFixtures.safeMeetingSpot;
import static com.naedong.friend.testsupport.TestFixtures.verifiedCompanion;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.lenient;

import com.naedong.friend.common.PolicyViolationException;
import com.naedong.friend.report.repository.BlockRepository;
import com.naedong.friend.user.domain.CompanionProfile;
import com.naedong.friend.user.domain.CompanionProfileStatus;
import com.naedong.friend.user.domain.UserVerification;
import com.naedong.friend.user.domain.VerificationStatus;
import com.naedong.friend.user.repository.CompanionProfileRepository;
import com.naedong.friend.user.repository.UserRepository;
import com.naedong.friend.user.repository.UserVerificationRepository;
import com.naedong.friend.booking.domain.SafeMeetingSpot;
import com.naedong.friend.booking.repository.SafeMeetingSpotRepository;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BookingPolicyServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    UserVerificationRepository verificationRepository;

    @Mock
    CompanionProfileRepository companionProfileRepository;

    @Mock
    SafeMeetingSpotRepository safeMeetingSpotRepository;

    @Mock
    BlockRepository blockRepository;

    BookingPolicyService service;

    @BeforeEach
    void setUp() {
        service = new BookingPolicyService(
                userRepository,
                verificationRepository,
                companionProfileRepository,
                safeMeetingSpotRepository,
                blockRepository
        );
        lenient().when(userRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customer()));
        lenient().when(userRepository.findById(COMPANION_ID)).thenReturn(Optional.of(companion()));
        lenient().when(companionProfileRepository.findByUserId(COMPANION_ID)).thenReturn(Optional.of(approvedProfile()));
        lenient().when(verificationRepository.findByUserId(COMPANION_ID)).thenReturn(Optional.of(verifiedCompanion()));
        lenient().when(safeMeetingSpotRepository.findById(MEETING_SPOT_ID)).thenReturn(Optional.of(safeMeetingSpot()));
        lenient().when(blockRepository.existsByBlockerIdAndBlockedUserId(CUSTOMER_ID, COMPANION_ID)).thenReturn(false);
        lenient().when(blockRepository.existsByBlockerIdAndBlockedUserId(COMPANION_ID, CUSTOMER_ID)).thenReturn(false);
    }

    @Test
    void unverifiedCompanionCannotReceiveBooking() {
        UserVerification verification = verifiedCompanion();
        verification.setIdentityStatus(VerificationStatus.PENDING);
        lenient().when(verificationRepository.findByUserId(COMPANION_ID)).thenReturn(Optional.of(verification));

        assertThatThrownBy(() -> service.validateBookingCreation(validCommand()))
                .isInstanceOf(PolicyViolationException.class)
                .hasMessageContaining("verification must be VERIFIED");
    }

    @Test
    void companionWithoutEmailAndPhoneVerificationCannotReceiveBooking() {
        UserVerification verification = verifiedCompanion();
        verification.setEmailVerified(false);
        verification.setPhoneVerified(false);
        lenient().when(verificationRepository.findByUserId(COMPANION_ID)).thenReturn(Optional.of(verification));

        assertThatThrownBy(() -> service.validateBookingCreation(validCommand()))
                .isInstanceOf(PolicyViolationException.class)
                .hasMessageContaining("email and phone verification");
    }

    @ParameterizedTest
    @EnumSource(value = CompanionProfileStatus.class, names = {"REJECTED", "SUSPENDED"})
    void rejectedOrSuspendedCompanionCannotReceiveBooking(CompanionProfileStatus status) {
        CompanionProfile profile = approvedProfile();
        profile.setStatus(status);
        lenient().when(companionProfileRepository.findByUserId(COMPANION_ID)).thenReturn(Optional.of(profile));

        assertThatThrownBy(() -> service.validateBookingCreation(validCommand()))
                .isInstanceOf(PolicyViolationException.class)
                .hasMessageContaining("approved companion profiles");
    }

    @Test
    void unsafeCategoryCannotBeBooked() {
        CreateBookingCommand command = new CreateBookingCommand(
                CUSTOMER_ID,
                COMPANION_ID,
                "ROMANTIC_DATE",
                MEETING_SPOT_ID,
                Instant.parse("2026-07-02T10:00:00Z"),
                Instant.parse("2026-07-02T11:00:00Z")
        );

        assertThatThrownBy(() -> service.validateBookingCreation(command))
                .isInstanceOf(PolicyViolationException.class)
                .hasMessageContaining("category is not allowed");
    }

    @Test
    void unsafeMeetingSpotCannotBeBooked() {
        SafeMeetingSpot spot = safeMeetingSpot();
        spot.setPrivateSpace(true);
        lenient().when(safeMeetingSpotRepository.findById(MEETING_SPOT_ID)).thenReturn(Optional.of(spot));

        assertThatThrownBy(() -> service.validateBookingCreation(validCommand()))
                .isInstanceOf(PolicyViolationException.class)
                .hasMessageContaining("Meeting spot does not satisfy");
    }

    @Test
    void customerCannotBookThemselves() {
        CreateBookingCommand command = new CreateBookingCommand(
                CUSTOMER_ID,
                CUSTOMER_ID,
                "COFFEE_BUDDY",
                MEETING_SPOT_ID,
                Instant.parse("2026-07-02T10:00:00Z"),
                Instant.parse("2026-07-02T11:00:00Z")
        );

        assertThatThrownBy(() -> service.validateBookingCreation(command))
                .isInstanceOf(PolicyViolationException.class)
                .hasMessageContaining("same user");
    }

    @Test
    void blockedUsersCannotBookEachOther() {
        lenient().when(blockRepository.existsByBlockerIdAndBlockedUserId(CUSTOMER_ID, COMPANION_ID)).thenReturn(true);

        assertThatThrownBy(() -> service.validateBookingCreation(validCommand()))
                .isInstanceOf(PolicyViolationException.class)
                .hasMessageContaining("Blocked users");
    }

    @Test
    void bookingOverMaxDurationIsRejected() {
        CreateBookingCommand command = new CreateBookingCommand(
                CUSTOMER_ID,
                COMPANION_ID,
                "COFFEE_BUDDY",
                MEETING_SPOT_ID,
                Instant.parse("2026-07-02T10:00:00Z"),
                Instant.parse("2026-07-02T12:01:00Z")
        );

        assertThatThrownBy(() -> service.validateBookingCreation(command))
                .isInstanceOf(PolicyViolationException.class)
                .hasMessageContaining("120 minutes");
    }

    private CreateBookingCommand validCommand() {
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
