package com.naedong.friend.testsupport;

import com.naedong.friend.booking.domain.Booking;
import com.naedong.friend.booking.domain.BookingStatus;
import com.naedong.friend.booking.domain.CompanionCategory;
import com.naedong.friend.booking.domain.SafeMeetingSpot;
import com.naedong.friend.booking.domain.SafeMeetingSpotType;
import com.naedong.friend.report.domain.Report;
import com.naedong.friend.report.domain.ReportReason;
import com.naedong.friend.report.domain.ReportStatus;
import com.naedong.friend.safety.domain.SafetyCard;
import com.naedong.friend.user.domain.CompanionProfile;
import com.naedong.friend.user.domain.CompanionProfileStatus;
import com.naedong.friend.user.domain.User;
import com.naedong.friend.user.domain.UserRole;
import com.naedong.friend.user.domain.UserStatus;
import com.naedong.friend.user.domain.UserVerification;
import com.naedong.friend.user.domain.VerificationStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public final class TestFixtures {

    public static final UUID CUSTOMER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    public static final UUID COMPANION_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");
    public static final UUID BOOKING_ID = UUID.fromString("00000000-0000-0000-0000-000000000003");
    public static final UUID MEETING_SPOT_ID = UUID.fromString("00000000-0000-0000-0000-000000000004");
    public static final UUID REPORT_ID = UUID.fromString("00000000-0000-0000-0000-000000000005");

    private TestFixtures() {
    }

    public static User customer() {
        User user = user(CUSTOMER_ID, UserRole.CUSTOMER, "customer@example.test", "+10000000001", "Customer One");
        return user;
    }

    public static User companion() {
        return user(COMPANION_ID, UserRole.COMPANION, "companion@example.test", "+10000000002", "Companion One");
    }

    public static User user(UUID id, UserRole role, String email, String phoneNumber, String displayName) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        user.setDisplayName(displayName);
        user.setRole(role);
        user.setStatus(UserStatus.ACTIVE);
        return user;
    }

    public static UserVerification verifiedCompanion() {
        UserVerification verification = new UserVerification();
        verification.setId(UUID.randomUUID());
        verification.setUserId(COMPANION_ID);
        verification.setEmailVerified(true);
        verification.setPhoneVerified(true);
        verification.setIdentityStatus(VerificationStatus.VERIFIED);
        verification.setLivenessStatus(VerificationStatus.VERIFIED);
        verification.setProviderName("test-provider");
        verification.setProviderReferenceId("provider-ref");
        verification.setVerifiedAt(Instant.parse("2026-01-01T00:00:00Z"));
        return verification;
    }

    public static CompanionProfile approvedProfile() {
        CompanionProfile profile = new CompanionProfile();
        profile.setId(UUID.randomUUID());
        profile.setUserId(COMPANION_ID);
        profile.setStatus(CompanionProfileStatus.APPROVED);
        profile.setBio("Public-purpose companion for safe MVP categories.");
        profile.setApprovedAt(Instant.parse("2026-01-01T00:00:00Z"));
        return profile;
    }

    public static SafeMeetingSpot safeMeetingSpot() {
        SafeMeetingSpot spot = new SafeMeetingSpot();
        spot.setId(MEETING_SPOT_ID);
        spot.setName("Central Library Cafe");
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
        return spot;
    }

    public static Booking requestedBooking() {
        return bookingWithStatus(BookingStatus.REQUESTED);
    }

    public static Booking bookingWithStatus(BookingStatus status) {
        Booking booking = new Booking();
        booking.setId(BOOKING_ID);
        booking.setCustomerId(CUSTOMER_ID);
        booking.setCompanionId(COMPANION_ID);
        booking.setCategory(CompanionCategory.COFFEE_BUDDY);
        booking.setMeetingSpotId(MEETING_SPOT_ID);
        booking.setStartTime(Instant.parse("2026-07-02T10:00:00Z"));
        booking.setEndTime(Instant.parse("2026-07-02T11:00:00Z"));
        booking.setStatus(status);
        return booking;
    }

    public static SafetyCard activeSafetyCard() {
        SafetyCard card = new SafetyCard();
        card.setId(UUID.randomUUID());
        card.setBookingId(BOOKING_ID);
        card.setPublicToken("safe-token");
        card.setExpiresAt(Instant.parse("2026-07-02T17:00:00Z"));
        return card;
    }

    public static Report savedReport() {
        Report report = new Report();
        report.setId(REPORT_ID);
        report.setReporterId(CUSTOMER_ID);
        report.setReportedUserId(COMPANION_ID);
        report.setBookingId(BOOKING_ID);
        report.setReason(ReportReason.HARASSMENT);
        report.setStatus(ReportStatus.OPEN);
        return report;
    }
}
