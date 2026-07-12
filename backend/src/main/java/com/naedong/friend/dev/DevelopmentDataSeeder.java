package com.naedong.friend.dev;

import com.naedong.friend.booking.domain.SafeMeetingSpot;
import com.naedong.friend.booking.domain.SafeMeetingSpotType;
import com.naedong.friend.booking.repository.SafeMeetingSpotRepository;
import com.naedong.friend.user.domain.CompanionProfile;
import com.naedong.friend.user.domain.CompanionProfileStatus;
import com.naedong.friend.user.domain.UserRole;
import com.naedong.friend.user.domain.UserVerification;
import com.naedong.friend.user.domain.VerificationStatus;
import com.naedong.friend.user.repository.CompanionProfileRepository;
import com.naedong.friend.user.repository.UserVerificationRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile("dev")
@ConditionalOnProperty(name = "friend.dev.seed-enabled", havingValue = "true")
public class DevelopmentDataSeeder implements ApplicationRunner {

    public static final UUID CUSTOMER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    public static final UUID COMPANION_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private static final String MEETING_SPOT_NAME = "Central Library Cafe";

    private final JdbcTemplate jdbcTemplate;
    private final UserVerificationRepository verificationRepository;
    private final CompanionProfileRepository companionProfileRepository;
    private final SafeMeetingSpotRepository meetingSpotRepository;

    public DevelopmentDataSeeder(
            JdbcTemplate jdbcTemplate,
            UserVerificationRepository verificationRepository,
            CompanionProfileRepository companionProfileRepository,
            SafeMeetingSpotRepository meetingSpotRepository
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.verificationRepository = verificationRepository;
        this.companionProfileRepository = companionProfileRepository;
        this.meetingSpotRepository = meetingSpotRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        seedUser(CUSTOMER_ID, UserRole.CUSTOMER, "customer@friend.local", "+10000000001", "Alex Morgan");
        seedUser(COMPANION_ID, UserRole.COMPANION, "companion@friend.local", "+10000000002", "Mina Park");
        seedCompanionVerification();
        seedCompanionProfile();
        seedMeetingSpot();
    }

    private void seedUser(UUID id, UserRole role, String email, String phoneNumber, String displayName) {
        jdbcTemplate.update("""
                insert into app_user (
                    id, email, phone_number, display_name, role, status, created_at, updated_at
                ) values (?, ?, ?, ?, ?, 'ACTIVE', current_timestamp, current_timestamp)
                on conflict (id) do nothing
                """, id, email, phoneNumber, displayName, role.name());
    }

    private void seedCompanionVerification() {
        if (verificationRepository.findByUserId(COMPANION_ID).isPresent()) {
            return;
        }
        UserVerification verification = new UserVerification();
        verification.setUserId(COMPANION_ID);
        verification.setEmailVerified(true);
        verification.setPhoneVerified(true);
        verification.setIdentityStatus(VerificationStatus.VERIFIED);
        verification.setLivenessStatus(VerificationStatus.VERIFIED);
        verification.setProviderName("friend-local-development");
        verification.setProviderReferenceId("local-development-only");
        verification.setVerifiedAt(Instant.now());
        verificationRepository.save(verification);
    }

    private void seedCompanionProfile() {
        if (companionProfileRepository.findByUserId(COMPANION_ID).isPresent()) {
            return;
        }
        CompanionProfile profile = new CompanionProfile();
        profile.setUserId(COMPANION_ID);
        profile.setStatus(CompanionProfileStatus.APPROVED);
        profile.setBio("Verified public-place companion for short, purpose-based bookings.");
        profile.setApprovedAt(Instant.now());
        companionProfileRepository.save(profile);
    }

    private void seedMeetingSpot() {
        if (meetingSpotRepository.existsByName(MEETING_SPOT_NAME)) {
            return;
        }
        SafeMeetingSpot spot = new SafeMeetingSpot();
        spot.setName(MEETING_SPOT_NAME);
        spot.setAddress("1 Public Square");
        spot.setLatitude(new BigDecimal("37.566500"));
        spot.setLongitude(new BigDecimal("126.978000"));
        spot.setType(SafeMeetingSpotType.LIBRARY);
        spot.setStaffPresent(true);
        spot.setWellLit(true);
        spot.setPublicEntrance(true);
        spot.setEasyExit(true);
        spot.setAlcoholCentered(false);
        spot.setPrivateSpace(false);
        spot.setActive(true);
        meetingSpotRepository.save(spot);
    }
}
