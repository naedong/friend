package com.naedong.friend.booking.api;

import com.naedong.friend.booking.domain.SafeMeetingSpotType;
import java.util.List;
import java.util.UUID;

public record BookingOptionsResponse(
        List<CompanionOption> companions,
        List<MeetingSpotOption> meetingSpots
) {

    public BookingOptionsResponse {
        companions = List.copyOf(companions);
        meetingSpots = List.copyOf(meetingSpots);
    }

    public record CompanionOption(
            UUID id,
            String displayName,
            String bio,
            boolean identityVerified,
            boolean livenessVerified
    ) {
    }

    public record MeetingSpotOption(
            UUID id,
            String name,
            String address,
            SafeMeetingSpotType type
    ) {
    }
}
