package com.naedong.friend.safety.service;

import java.time.Instant;

public record SafetyCardView(
        String safetyCardReference,
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
