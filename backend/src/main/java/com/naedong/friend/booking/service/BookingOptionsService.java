package com.naedong.friend.booking.service;

import com.naedong.friend.booking.api.BookingOptionsResponse;
import com.naedong.friend.booking.api.BookingOptionsResponse.CompanionOption;
import com.naedong.friend.booking.api.BookingOptionsResponse.MeetingSpotOption;
import com.naedong.friend.booking.repository.SafeMeetingSpotRepository;
import com.naedong.friend.common.DomainNotFoundException;
import com.naedong.friend.common.PolicyViolationException;
import com.naedong.friend.report.repository.BlockRepository;
import com.naedong.friend.user.domain.CompanionProfileStatus;
import com.naedong.friend.user.domain.User;
import com.naedong.friend.user.domain.UserRole;
import com.naedong.friend.user.domain.UserStatus;
import com.naedong.friend.user.domain.UserVerification;
import com.naedong.friend.user.domain.VerificationStatus;
import com.naedong.friend.user.repository.CompanionProfileRepository;
import com.naedong.friend.user.repository.UserRepository;
import com.naedong.friend.user.repository.UserVerificationRepository;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingOptionsService {

    private final UserRepository userRepository;
    private final UserVerificationRepository verificationRepository;
    private final CompanionProfileRepository companionProfileRepository;
    private final SafeMeetingSpotRepository meetingSpotRepository;
    private final BlockRepository blockRepository;

    public BookingOptionsService(
            UserRepository userRepository,
            UserVerificationRepository verificationRepository,
            CompanionProfileRepository companionProfileRepository,
            SafeMeetingSpotRepository meetingSpotRepository,
            BlockRepository blockRepository
    ) {
        this.userRepository = userRepository;
        this.verificationRepository = verificationRepository;
        this.companionProfileRepository = companionProfileRepository;
        this.meetingSpotRepository = meetingSpotRepository;
        this.blockRepository = blockRepository;
    }

    @Transactional(readOnly = true)
    public BookingOptionsResponse getOptions(UUID actorUserId) {
        requireActiveActor(actorUserId);

        List<CompanionOption> companions = companionProfileRepository
                .findAllByStatus(CompanionProfileStatus.APPROVED)
                .stream()
                .filter(profile -> !profile.getUserId().equals(actorUserId))
                .filter(profile -> !isBlocked(actorUserId, profile.getUserId()))
                .map(profile -> userRepository.findById(profile.getUserId())
                        .flatMap(user -> verificationRepository.findByUserId(user.getId())
                                .filter(verification -> isEligible(user, verification))
                                .map(verification -> new CompanionOption(
                                        user.getId(),
                                        user.getDisplayName(),
                                        profile.getBio(),
                                        true,
                                        true
                                )))
                        .orElse(null))
                .filter(option -> option != null)
                .sorted(Comparator.comparing(CompanionOption::displayName, String.CASE_INSENSITIVE_ORDER))
                .toList();

        List<MeetingSpotOption> meetingSpots = meetingSpotRepository
                .findAllByActiveTrueOrderByNameAsc()
                .stream()
                .filter(spot -> spot.passesSafetyRules())
                .map(spot -> new MeetingSpotOption(
                        spot.getId(),
                        spot.getName(),
                        spot.getAddress(),
                        spot.getType()
                ))
                .toList();

        return new BookingOptionsResponse(companions, meetingSpots);
    }

    private void requireActiveActor(UUID actorUserId) {
        if (actorUserId == null) {
            throw new PolicyViolationException("Authenticated user is required.");
        }
        User actor = userRepository.findById(actorUserId)
                .orElseThrow(() -> new DomainNotFoundException("Authenticated user was not found."));
        if (actor.getStatus() != UserStatus.ACTIVE) {
            throw new PolicyViolationException("Only active users can browse booking options.");
        }
    }

    private boolean isBlocked(UUID actorUserId, UUID companionId) {
        return blockRepository.existsByBlockerIdAndBlockedUserId(actorUserId, companionId)
                || blockRepository.existsByBlockerIdAndBlockedUserId(companionId, actorUserId);
    }

    private boolean isEligible(User user, UserVerification verification) {
        return user.getStatus() == UserStatus.ACTIVE
                && user.getRole() == UserRole.COMPANION
                && verification.isEmailVerified()
                && verification.isPhoneVerified()
                && verification.getIdentityStatus() == VerificationStatus.VERIFIED
                && verification.getLivenessStatus() == VerificationStatus.VERIFIED;
    }
}
