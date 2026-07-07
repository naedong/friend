package com.naedong.friend.safety.service;

import com.naedong.friend.booking.domain.Booking;
import com.naedong.friend.booking.domain.SafeMeetingSpot;
import com.naedong.friend.booking.repository.BookingRepository;
import com.naedong.friend.booking.repository.SafeMeetingSpotRepository;
import com.naedong.friend.common.DomainNotFoundException;
import com.naedong.friend.safety.domain.SafetyCard;
import com.naedong.friend.safety.repository.SafetyCardRepository;
import com.naedong.friend.user.domain.User;
import com.naedong.friend.user.domain.UserVerification;
import com.naedong.friend.user.domain.VerificationStatus;
import com.naedong.friend.user.repository.UserRepository;
import com.naedong.friend.user.repository.UserVerificationRepository;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SafetyCardService {

    private static final Duration SAFETY_CARD_TTL_AFTER_BOOKING = Duration.ofHours(6);

    private final SafetyCardRepository safetyCardRepository;
    private final BookingRepository bookingRepository;
    private final SafeMeetingSpotRepository safeMeetingSpotRepository;
    private final UserRepository userRepository;
    private final UserVerificationRepository userVerificationRepository;
    private final Clock clock;

    public SafetyCardService(
            SafetyCardRepository safetyCardRepository,
            BookingRepository bookingRepository,
            SafeMeetingSpotRepository safeMeetingSpotRepository,
            UserRepository userRepository,
            UserVerificationRepository userVerificationRepository,
            Clock clock
    ) {
        this.safetyCardRepository = safetyCardRepository;
        this.bookingRepository = bookingRepository;
        this.safeMeetingSpotRepository = safeMeetingSpotRepository;
        this.userRepository = userRepository;
        this.userVerificationRepository = userVerificationRepository;
        this.clock = clock;
    }

    @Transactional
    public SafetyCard generateSafetyCard(UUID bookingId) {
        return safetyCardRepository.findByBookingId(bookingId)
                .orElseGet(() -> createSafetyCard(bookingId));
    }

    @Transactional
    public SafetyCardView getSafetyCardView(String publicToken) {
        SafetyCardToken.requireValid(publicToken);
        SafetyCard safetyCard = safetyCardRepository.findByPublicToken(publicToken)
                .orElseThrow(() -> new DomainNotFoundException("Safety card not found."));
        if (safetyCard.getExpiresAt().isBefore(Instant.now(clock))) {
            throw new SafetyCardExpiredException("Safety card has expired.");
        }

        Booking booking = bookingRepository.findById(safetyCard.getBookingId())
                .orElseThrow(() -> new DomainNotFoundException("Booking not found for safety card."));
        SafeMeetingSpot meetingSpot = safeMeetingSpotRepository.findById(booking.getMeetingSpotId())
                .orElseThrow(() -> new DomainNotFoundException("Meeting spot not found for safety card."));
        User companion = userRepository.findById(booking.getCompanionId())
                .orElseThrow(() -> new DomainNotFoundException("Companion not found for safety card."));
        User customer = userRepository.findById(booking.getCustomerId())
                .orElseThrow(() -> new DomainNotFoundException("Customer not found for safety card."));
        UserVerification companionVerification = userVerificationRepository.findByUserId(booking.getCompanionId())
                .orElse(null);

        return new SafetyCardView(
                ensurePublicReference(safetyCard),
                booking.getCategory().name(),
                new SafetyCardView.MeetingSpotView(meetingSpot.getName(), meetingSpot.getAddress()),
                booking.getStartTime(),
                booking.getEndTime(),
                companion.getDisplayName(),
                customer.getDisplayName(),
                new SafetyCardView.VerificationSummary(
                        companionVerification != null && companionVerification.getIdentityStatus() == VerificationStatus.VERIFIED,
                        companionVerification != null && companionVerification.getLivenessStatus() == VerificationStatus.VERIFIED
                ),
                "Stay in the public meeting spot. Use the in-app report flow if anything feels unsafe. Contact local emergency services directly if there is immediate danger."
        );
    }

    private SafetyCard createSafetyCard(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new DomainNotFoundException("Booking not found: " + bookingId));
        SafetyCard safetyCard = new SafetyCard();
        safetyCard.setBookingId(bookingId);
        safetyCard.setPublicToken(SafetyCardToken.newToken());
        safetyCard.setPublicReference(newPublicReference());
        safetyCard.setExpiresAt(booking.getEndTime().plus(SAFETY_CARD_TTL_AFTER_BOOKING));
        return safetyCardRepository.save(safetyCard);
    }

    private String ensurePublicReference(SafetyCard safetyCard) {
        if (safetyCard.getPublicReference() == null || safetyCard.getPublicReference().isBlank()) {
            safetyCard.setPublicReference(newPublicReference());
            safetyCardRepository.save(safetyCard);
        }
        return safetyCard.getPublicReference();
    }

    private String newPublicReference() {
        return "SC-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }
}
