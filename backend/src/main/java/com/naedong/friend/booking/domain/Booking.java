package com.naedong.friend.booking.domain;

import com.naedong.friend.common.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "booking")
public class Booking extends AuditableEntity {

    @Column(nullable = false)
    private UUID customerId;

    @Column(nullable = false)
    private UUID companionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CompanionCategory category;

    @Column(nullable = false)
    private UUID meetingSpotId;

    @Column(nullable = false)
    private Instant startTime;

    @Column(nullable = false)
    private Instant endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public UUID getCompanionId() {
        return companionId;
    }

    public void setCompanionId(UUID companionId) {
        this.companionId = companionId;
    }

    public CompanionCategory getCategory() {
        return category;
    }

    public void setCategory(CompanionCategory category) {
        this.category = category;
    }

    public UUID getMeetingSpotId() {
        return meetingSpotId;
    }

    public void setMeetingSpotId(UUID meetingSpotId) {
        this.meetingSpotId = meetingSpotId;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }
}
