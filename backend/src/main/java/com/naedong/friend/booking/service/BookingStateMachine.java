package com.naedong.friend.booking.service;

import com.naedong.friend.booking.domain.Booking;
import com.naedong.friend.booking.domain.BookingStatus;
import com.naedong.friend.booking.repository.BookingRepository;
import com.naedong.friend.common.RequestMetadata;
import com.naedong.friend.safety.service.AuditLogService;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingStateMachine {

    private static final Map<BookingStatus, Set<BookingStatus>> TRANSITIONS = new EnumMap<>(BookingStatus.class);

    static {
        TRANSITIONS.put(BookingStatus.REQUESTED, EnumSet.of(
                BookingStatus.ACCEPTED,
                BookingStatus.REJECTED,
                BookingStatus.CANCELLED,
                BookingStatus.REPORTED
        ));
        TRANSITIONS.put(BookingStatus.ACCEPTED, EnumSet.of(
                BookingStatus.CHECKED_IN,
                BookingStatus.CANCELLED,
                BookingStatus.REPORTED
        ));
        TRANSITIONS.put(BookingStatus.CHECKED_IN, EnumSet.of(
                BookingStatus.IN_PROGRESS,
                BookingStatus.REPORTED
        ));
        TRANSITIONS.put(BookingStatus.IN_PROGRESS, EnumSet.of(
                BookingStatus.CHECKOUT_PENDING,
                BookingStatus.REPORTED
        ));
        TRANSITIONS.put(BookingStatus.CHECKOUT_PENDING, EnumSet.of(
                BookingStatus.COMPLETED,
                BookingStatus.REPORTED
        ));
        TRANSITIONS.put(BookingStatus.REPORTED, EnumSet.of(BookingStatus.SAFETY_HOLD));
        TRANSITIONS.put(BookingStatus.SAFETY_HOLD, EnumSet.of(BookingStatus.PAYOUT_HOLD));
        TRANSITIONS.put(BookingStatus.REJECTED, EnumSet.noneOf(BookingStatus.class));
        TRANSITIONS.put(BookingStatus.CANCELLED, EnumSet.noneOf(BookingStatus.class));
        TRANSITIONS.put(BookingStatus.COMPLETED, EnumSet.noneOf(BookingStatus.class));
        TRANSITIONS.put(BookingStatus.PAYOUT_HOLD, EnumSet.noneOf(BookingStatus.class));
    }

    private final BookingRepository bookingRepository;
    private final AuditLogService auditLogService;

    public BookingStateMachine(BookingRepository bookingRepository, AuditLogService auditLogService) {
        this.bookingRepository = bookingRepository;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public Booking transition(
            Booking booking,
            BookingStatus nextStatus,
            UUID actorUserId,
            String reason,
            RequestMetadata metadata
    ) {
        BookingStatus currentStatus = validateTransition(booking, nextStatus);
        booking.setStatus(nextStatus);
        Booking saved = bookingRepository.save(booking);
        auditLogService.record(
                actorUserId,
                "BOOKING_STATUS_CHANGED",
                "BOOKING",
                booking.getId(),
                currentStatus + " -> " + nextStatus + ": " + reason,
                metadata
        );
        return saved;
    }

    @Transactional
    public Booking transitionSystem(Booking booking, BookingStatus nextStatus, String reason) {
        BookingStatus currentStatus = validateTransition(booking, nextStatus);
        booking.setStatus(nextStatus);
        Booking saved = bookingRepository.save(booking);
        auditLogService.recordSystem(
                "BOOKING_STATUS_CHANGED",
                "BOOKING",
                booking.getId(),
                currentStatus + " -> " + nextStatus + ": " + reason
        );
        return saved;
    }

    private BookingStatus validateTransition(Booking booking, BookingStatus nextStatus) {
        BookingStatus currentStatus = booking.getStatus();
        Set<BookingStatus> allowedNextStatuses = TRANSITIONS.getOrDefault(currentStatus, Set.of());
        if (!allowedNextStatuses.contains(nextStatus)) {
            throw new InvalidBookingStateTransitionException(
                    "Invalid booking state transition: " + currentStatus + " -> " + nextStatus
            );
        }
        return currentStatus;
    }
}
