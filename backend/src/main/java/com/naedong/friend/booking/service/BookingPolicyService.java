package com.naedong.friend.booking.service;

import com.naedong.friend.booking.domain.CompanionCategory;
import com.naedong.friend.booking.domain.SafeMeetingSpot;
import com.naedong.friend.booking.repository.SafeMeetingSpotRepository;
import com.naedong.friend.common.DomainNotFoundException;
import com.naedong.friend.common.PolicyViolationException;
import com.naedong.friend.report.repository.BlockRepository;
import com.naedong.friend.user.domain.CompanionProfile;
import com.naedong.friend.user.domain.CompanionProfileStatus;
import com.naedong.friend.user.domain.User;
import com.naedong.friend.user.domain.UserRole;
import com.naedong.friend.user.domain.UserStatus;
import com.naedong.friend.user.domain.UserVerification;
import com.naedong.friend.user.domain.VerificationStatus;
import com.naedong.friend.user.repository.CompanionProfileRepository;
import com.naedong.friend.user.repository.UserRepository;
import com.naedong.friend.user.repository.UserVerificationRepository;
import java.time.Duration;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingPolicyService {

    public static final Duration MVP_MAX_BOOKING_DURATION = Duration.ofMinutes(120);

    private final UserRepository userRepository;
    private final UserVerificationRepository userVerificationRepository;
    private final CompanionProfileRepository companionProfileRepository;
    private final SafeMeetingSpotRepository safeMeetingSpotRepository;
    private final BlockRepository blockRepository;

    public BookingPolicyService(
            UserRepository userRepository,
            UserVerificationRepository userVerificationRepository,
            CompanionProfileRepository companionProfileRepository,
            SafeMeetingSpotRepository safeMeetingSpotRepository,
            BlockRepository blockRepository
    ) {
        this.userRepository = userRepository;
        this.userVerificationRepository = userVerificationRepository;
        this.companionProfileRepository = companionProfileRepository;
        this.safeMeetingSpotRepository = safeMeetingSpotRepository;
        this.blockRepository = blockRepository;
    }

    @Transactional(readOnly = true)
    public ValidatedBookingCreation validateBookingCreation(CreateBookingCommand command) {
        if (command.customerId() == null) {
            throw new PolicyViolationException("Anonymous users cannot create bookings.");
        }
        if (command.customerId().equals(command.companionId())) {
            throw new PolicyViolationException("Customer and companion cannot be the same user.");
        }

        CompanionCategory category = validateAllowedCategory(command.categoryName());
        validateDuration(command.startTime(), command.endTime());
        validateBlockedRelationship(command.customerId(), command.companionId());
        validateCustomer(command.customerId());
        validateCompanionEligibility(command.companionId());
        validateSafeMeetingSpot(command.meetingSpotId());

        return new ValidatedBookingCreation(
                command.customerId(),
                command.companionId(),
                category,
                command.meetingSpotId(),
                command.startTime(),
                command.endTime()
        );
    }

    public CompanionCategory validateAllowedCategory(String categoryName) {
        return CompanionCategory.fromAllowedName(categoryName);
    }

    @Transactional(readOnly = true)
    public SafeMeetingSpot validateSafeMeetingSpot(UUID meetingSpotId) {
        SafeMeetingSpot meetingSpot = safeMeetingSpotRepository.findById(meetingSpotId)
                .orElseThrow(() -> new DomainNotFoundException("Meeting spot not found: " + meetingSpotId));
        if (!meetingSpot.passesSafetyRules()) {
            throw new PolicyViolationException("Meeting spot does not satisfy public safety requirements.");
        }
        return meetingSpot;
    }

    @Transactional(readOnly = true)
    public void validateCompanionEligibility(UUID companionId) {
        User companion = userRepository.findById(companionId)
                .orElseThrow(() -> new DomainNotFoundException("Companion user not found: " + companionId));
        if (companion.getStatus() != UserStatus.ACTIVE || companion.getRole() != UserRole.COMPANION) {
            throw new PolicyViolationException("Companion must be an active companion user.");
        }

        CompanionProfile profile = companionProfileRepository.findByUserId(companionId)
                .orElseThrow(() -> new PolicyViolationException("Companion profile is required before bookings."));
        if (profile.getStatus() != CompanionProfileStatus.APPROVED) {
            throw new PolicyViolationException("Only approved companion profiles can receive bookings.");
        }

        UserVerification verification = userVerificationRepository.findByUserId(companionId)
                .orElseThrow(() -> new PolicyViolationException("Companion verification is required before bookings."));
        if (!verification.isEmailVerified() || !verification.isPhoneVerified()) {
            throw new PolicyViolationException("Companion email and phone verification must be complete.");
        }
        if (verification.getIdentityStatus() != VerificationStatus.VERIFIED
                || verification.getLivenessStatus() != VerificationStatus.VERIFIED) {
            throw new PolicyViolationException("Companion identity and liveness verification must be VERIFIED.");
        }
    }

    @Transactional(readOnly = true)
    public void validateBlockedRelationship(UUID customerId, UUID companionId) {
        boolean blocked = blockRepository.existsByBlockerIdAndBlockedUserId(customerId, companionId)
                || blockRepository.existsByBlockerIdAndBlockedUserId(companionId, customerId);
        if (blocked) {
            throw new PolicyViolationException("Blocked users cannot create future bookings with each other.");
        }
    }

    public void validateDuration(java.time.Instant startTime, java.time.Instant endTime) {
        if (startTime == null || endTime == null || !endTime.isAfter(startTime)) {
            throw new PolicyViolationException("Booking time range must have a valid start and end.");
        }
        Duration duration = Duration.between(startTime, endTime);
        if (duration.compareTo(MVP_MAX_BOOKING_DURATION) > 0) {
            throw new PolicyViolationException("Booking duration exceeds the MVP maximum of 120 minutes.");
        }
    }

    private void validateCustomer(UUID customerId) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new DomainNotFoundException("Customer user not found: " + customerId));
        if (customer.getStatus() != UserStatus.ACTIVE) {
            throw new PolicyViolationException("Customer must be active to create a booking.");
        }
    }
}
