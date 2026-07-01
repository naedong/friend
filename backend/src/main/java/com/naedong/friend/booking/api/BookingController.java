package com.naedong.friend.booking.api;

import com.naedong.friend.booking.domain.Booking;
import com.naedong.friend.booking.service.BookingService;
import com.naedong.friend.booking.service.CreateBookingCommand;
import com.naedong.friend.common.api.RequestMetadataFactory;
import com.naedong.friend.security.ActorProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final ActorProvider actorProvider;

    public BookingController(BookingService bookingService, ActorProvider actorProvider) {
        this.bookingService = bookingService;
        this.actorProvider = actorProvider;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse createBooking(
            @Valid @RequestBody CreateBookingRequest body,
            HttpServletRequest request
    ) {
        UUID actorUserId = actorProvider.currentActorId(request);
        Booking booking = bookingService.createBooking(new CreateBookingCommand(
                actorUserId,
                body.companionId(),
                body.category(),
                body.meetingSpotId(),
                body.startTime(),
                body.endTime()
        ), RequestMetadataFactory.from(request));
        return BookingResponse.from(booking);
    }

    @PostMapping("/{id}/accept")
    public BookingResponse acceptBooking(@PathVariable UUID id, HttpServletRequest request) {
        UUID actorUserId = actorProvider.currentActorId(request);
        Booking booking = bookingService.acceptBooking(id, actorUserId, RequestMetadataFactory.from(request));
        return BookingResponse.from(booking);
    }

    @PostMapping("/{id}/check-in")
    public BookingResponse checkIn(
            @PathVariable UUID id,
            @Valid @RequestBody(required = false) LocationRequest body,
            HttpServletRequest request
    ) {
        UUID actorUserId = actorProvider.currentActorId(request);
        LocationRequest location = body == null ? new LocationRequest(null, null) : body;
        Booking booking = bookingService.checkIn(
                id,
                actorUserId,
                location.latitude(),
                location.longitude(),
                RequestMetadataFactory.from(request)
        );
        return BookingResponse.from(booking);
    }

    @PostMapping("/{id}/check-out")
    public BookingResponse checkOut(
            @PathVariable UUID id,
            @Valid @RequestBody(required = false) LocationRequest body,
            HttpServletRequest request
    ) {
        UUID actorUserId = actorProvider.currentActorId(request);
        LocationRequest location = body == null ? new LocationRequest(null, null) : body;
        Booking booking = bookingService.checkOut(
                id,
                actorUserId,
                location.latitude(),
                location.longitude(),
                RequestMetadataFactory.from(request)
        );
        return BookingResponse.from(booking);
    }
}
