package com.naedong.friend.safety.service;

import java.time.Instant;
import java.util.UUID;

public record SafetyCardView(
        UUID bookingId,
        String category,
        MeetingSpotView meetingSpot,
        Instant startTime,
        Instant endTime,
        String companionDisplayName,
        String customerDisplayName,
        VerificationSummary verificationSummary,
        String emergencyInstructions
) {

    public record MeetingSpotView(String name, String address) {
    }

    public record VerificationSummary(
            boolean companionIdentityVerified,
            boolean companionLivenessVerified
    ) {
    }
}
