package com.naedong.friend.safety.service;

import com.naedong.friend.booking.domain.Booking;
import com.naedong.friend.booking.domain.BookingStatus;
import com.naedong.friend.booking.repository.BookingRepository;
import com.naedong.friend.booking.service.BookingStateMachine;
import com.naedong.friend.common.DomainNotFoundException;
import com.naedong.friend.common.RequestMetadata;
import com.naedong.friend.gateway.NotificationGateway;
import com.naedong.friend.gateway.TrustedContactAlertGateway;
import com.naedong.friend.safety.domain.BookingSafetyEvent;
import com.naedong.friend.safety.domain.BookingSafetyEventType;
import com.naedong.friend.safety.domain.SafetyEventSeverity;
import com.naedong.friend.safety.repository.BookingSafetyEventRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SafetyEscalationService {

    private final BookingRepository bookingRepository;
    private final BookingStateMachine bookingStateMachine;
    private final BookingSafetyEventRepository safetyEventRepository;
    private final TrustedContactAlertGateway trustedContactAlertGateway;
    private final NotificationGateway notificationGateway;

    public SafetyEscalationService(
            BookingRepository bookingRepository,
            BookingStateMachine bookingStateMachine,
            BookingSafetyEventRepository safetyEventRepository,
            TrustedContactAlertGateway trustedContactAlertGateway,
            NotificationGateway notificationGateway
    ) {
        this.bookingRepository = bookingRepository;
        this.bookingStateMachine = bookingStateMachine;
        this.safetyEventRepository = safetyEventRepository;
        this.trustedContactAlertGateway = trustedContactAlertGateway;
        this.notificationGateway = notificationGateway;
    }

    @Transactional
    public List<BookingSafetyEvent> detectMissedCheckouts(Instant now) {
        return bookingRepository.findByStatusInAndEndTimeBefore(
                        List.of(BookingStatus.IN_PROGRESS, BookingStatus.CHECKOUT_PENDING),
                        now
                )
                .stream()
                .flatMap(booking -> handleMissedCheckout(booking.getId(), now).stream())
                .toList();
    }

    @Transactional
    public List<BookingSafetyEvent> handleMissedCheckout(UUID bookingId, Instant detectedAt) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new DomainNotFoundException("Booking not found: " + bookingId));

        BookingSafetyEvent missed = createSafetyEvent(
                bookingId,
                BookingSafetyEventType.CHECKOUT_MISSED,
                SafetyEventSeverity.HIGH,
                "Checkout was missed at " + detectedAt + "."
        );
        BookingSafetyEvent trustedContactAlert = createSafetyEvent(
                bookingId,
                BookingSafetyEventType.TRUSTED_CONTACT_ALERTED,
                SafetyEventSeverity.HIGH,
                "Trusted contacts should be alerted for both booking participants."
        );
        BookingSafetyEvent moderatorReview = createSafetyEvent(
                bookingId,
                BookingSafetyEventType.MODERATOR_REVIEW_CREATED,
                SafetyEventSeverity.HIGH,
                "Moderator review created after missed checkout."
        );

        trustedContactAlertGateway.alertTrustedContacts(bookingId, booking.getCustomerId(), "Missed checkout detected.");
        trustedContactAlertGateway.alertTrustedContacts(bookingId, booking.getCompanionId(), "Missed checkout detected.");
        notificationGateway.notifyModerator(bookingId, "Missed checkout detected.");

        Booking current = booking;
        if (current.getStatus() == BookingStatus.IN_PROGRESS) {
            current = bookingStateMachine.transition(current, BookingStatus.CHECKOUT_PENDING, null, "Missed checkout detected", RequestMetadata.empty());
        }
        if (current.getStatus() == BookingStatus.CHECKOUT_PENDING) {
            current = bookingStateMachine.transition(current, BookingStatus.REPORTED, null, "Missed checkout escalated", RequestMetadata.empty());
        }
        if (current.getStatus() == BookingStatus.REPORTED) {
            bookingStateMachine.transition(current, BookingStatus.SAFETY_HOLD, null, "Safety hold after missed checkout", RequestMetadata.empty());
        }

        return List.of(missed, trustedContactAlert, moderatorReview);
    }

    private BookingSafetyEvent createSafetyEvent(
            UUID bookingId,
            BookingSafetyEventType type,
            SafetyEventSeverity severity,
            String message
    ) {
        BookingSafetyEvent event = new BookingSafetyEvent();
        event.setBookingId(bookingId);
        event.setType(type);
        event.setSeverity(severity);
        event.setMessage(message);
        return safetyEventRepository.save(event);
    }
}
