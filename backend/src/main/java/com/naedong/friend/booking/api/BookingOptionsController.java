package com.naedong.friend.booking.api;

import com.naedong.friend.booking.service.BookingOptionsService;
import com.naedong.friend.security.ActorProvider;
import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/booking-options")
public class BookingOptionsController {

    private final BookingOptionsService bookingOptionsService;
    private final ActorProvider actorProvider;

    public BookingOptionsController(BookingOptionsService bookingOptionsService, ActorProvider actorProvider) {
        this.bookingOptionsService = bookingOptionsService;
        this.actorProvider = actorProvider;
    }

    @GetMapping
    public BookingOptionsResponse getOptions(HttpServletRequest request) {
        UUID actorUserId = actorProvider.currentActorId(request);
        return bookingOptionsService.getOptions(actorUserId);
    }
}
