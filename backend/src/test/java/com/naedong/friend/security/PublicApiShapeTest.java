package com.naedong.friend.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.naedong.friend.booking.api.CreateBookingRequest;
import com.naedong.friend.report.api.CreateReportRequest;
import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class PublicApiShapeTest {

    @Test
    void createBookingRequestDoesNotAcceptClientControlledSafetyFields() {
        Set<String> fields = recordFields(CreateBookingRequest.class);

        assertThat(fields).containsExactlyInAnyOrder(
                "companionId",
                "category",
                "meetingSpotId",
                "startTime",
                "endTime"
        );
        assertThat(fields).doesNotContain(
                "customerId",
                "status",
                "role",
                "verificationStatus",
                "identityStatus",
                "livenessStatus",
                "price",
                "safetyStatus"
        );
    }

    @Test
    void createReportRequestDoesNotAcceptClientControlledReportStatus() {
        Set<String> fields = recordFields(CreateReportRequest.class);

        assertThat(fields).containsExactlyInAnyOrder(
                "reportedUserId",
                "reason",
                "blockReportedUser"
        );
        assertThat(fields).doesNotContain(
                "status",
                "bookingStatus",
                "safetyHold",
                "payoutHold"
        );
    }

    private Set<String> recordFields(Class<? extends Record> recordType) {
        return Arrays.stream(recordType.getRecordComponents())
                .map(RecordComponent::getName)
                .collect(Collectors.toSet());
    }
}
