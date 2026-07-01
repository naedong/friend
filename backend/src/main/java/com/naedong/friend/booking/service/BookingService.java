package com.naedong.friend.booking.service;

import com.naedong.friend.booking.domain.Booking;
import com.naedong.friend.booking.domain.BookingCheckin;
import com.naedong.friend.booking.domain.BookingCheckinType;
import com.naedong.friend.booking.domain.BookingStatus;
import com.naedong.friend.booking.repository.BookingCheckinRepository;
import com.naedong.friend.booking.repository.BookingRepository;
import com.naedong.friend.common.DomainNotFoundException;
import com.naedong.friend.common.PolicyViolationException;
import com.naedong.friend.common.RequestMetadata;
import com.naedong.friend.safety.service.AuditLogService;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingService {

    private final BookingPolicyService bookingPolicyService;
    private final BookingRepository bookingRepository;
    private final BookingCheckinRepository bookingCheckinRepository;
    private final BookingStateMachine bookingStateMachine;
    private final AuditLogService auditLogService;

    public BookingService(
            BookingPolicyService bookingPolicyService,
            BookingRepository bookingRepository,
            BookingCheckinRepository bookingCheckinRepository,
            BookingStateMachine bookingStateMachine,
            AuditLogService auditLogService
    ) {
        this.bookingPolicyService = bookingPolicyService;
        this.bookingRepository = bookingRepository;
        this.bookingCheckinRepository = bookingCheckinRepository;
        this.bookingStateMachine = bookingStateMachine;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public Booking createBooking(CreateBookingCommand command) {
        return createBooking(command, RequestMetadata.empty());
    }

    @Transactional
    public Booking createBooking(CreateBookingCommand command, RequestMetadata metadata) {
        ValidatedBookingCreation validated = bookingPolicyService.validateBookingCreation(command);
        Booking booking = new Booking();
        booking.setCustomerId(validated.customerId());
        booking.setCompanionId(validated.companionId());
        booking.setCategory(validated.category());
        booking.setMeetingSpotId(validated.meetingSpotId());
        booking.setStartTime(validated.startTime());
        booking.setEndTime(validated.endTime());
        booking.setStatus(BookingStatus.REQUESTED);
        Booking saved = bookingRepository.save(booking);
        auditLogService.record(
                saved.getCustomerId(),
                "BOOKING_REQUESTED",
                "BOOKING",
                saved.getId(),
                "Customer requested booking",
                metadata
        );
        return saved;
    }

    @Transactional
    public Booking acceptBooking(UUID bookingId, UUID actorUserId, RequestMetadata metadata) {
        Booking booking = findBooking(bookingId);
        requireCompanionActor(booking, actorUserId);
        return bookingStateMachine.transition(booking, BookingStatus.ACCEPTED, actorUserId, "Companion accepted booking", metadata);
    }

    @Transactional
    public Booking checkIn(UUID bookingId, UUID actorUserId, BigDecimal latitude, BigDecimal longitude, RequestMetadata metadata) {
        Booking booking = findBooking(bookingId);
        requireBookingParticipant(booking, actorUserId);
        createCheckin(bookingId, actorUserId, BookingCheckinType.CHECK_IN, latitude, longitude);
        auditLogService.record(actorUserId, "BOOKING_CHECK_IN", "BOOKING", bookingId, "Participant checked in", metadata);
        Booking checkedIn = bookingStateMachine.transition(booking, BookingStatus.CHECKED_IN, actorUserId, "Participant checked in", metadata);
        return bookingStateMachine.transition(checkedIn, BookingStatus.IN_PROGRESS, actorUserId, "Booking started after check-in", metadata);
    }

    @Transactional
    public Booking checkOut(UUID bookingId, UUID actorUserId, BigDecimal latitude, BigDecimal longitude, RequestMetadata metadata) {
        Booking booking = findBooking(bookingId);
        requireBookingParticipant(booking, actorUserId);
        createCheckin(bookingId, actorUserId, BookingCheckinType.CHECK_OUT, latitude, longitude);
        auditLogService.record(actorUserId, "BOOKING_CHECK_OUT", "BOOKING", bookingId, "Participant checked out", metadata);
        return bookingStateMachine.transition(booking, BookingStatus.CHECKOUT_PENDING, actorUserId, "Participant checked out", metadata);
    }

    private Booking findBooking(UUID bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new DomainNotFoundException("Booking not found: " + bookingId));
    }

    private void createCheckin(
            UUID bookingId,
            UUID actorUserId,
            BookingCheckinType type,
            BigDecimal latitude,
            BigDecimal longitude
    ) {
        BookingCheckin checkin = new BookingCheckin();
        checkin.setBookingId(bookingId);
        checkin.setUserId(actorUserId);
        checkin.setType(type);
        checkin.setLatitude(latitude);
        checkin.setLongitude(longitude);
        bookingCheckinRepository.save(checkin);
    }

    private void requireCompanionActor(Booking booking, UUID actorUserId) {
        if (!booking.getCompanionId().equals(actorUserId)) {
            throw new PolicyViolationException("Only the assigned companion can accept the booking.");
        }
    }

    private void requireBookingParticipant(Booking booking, UUID actorUserId) {
        if (!booking.getCustomerId().equals(actorUserId) && !booking.getCompanionId().equals(actorUserId)) {
            throw new PolicyViolationException("Only booking participants can perform this action.");
        }
    }
}
