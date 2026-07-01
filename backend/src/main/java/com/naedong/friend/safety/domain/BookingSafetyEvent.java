package com.naedong.friend.safety.domain;

import com.naedong.friend.common.CreatedAtEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "booking_safety_event")
public class BookingSafetyEvent extends CreatedAtEntity {

    @Column(nullable = false)
    private UUID bookingId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingSafetyEventType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SafetyEventSeverity severity;

    @Column(nullable = false, length = 1_000)
    private String message;

    public UUID getBookingId() {
        return bookingId;
    }

    public void setBookingId(UUID bookingId) {
        this.bookingId = bookingId;
    }

    public BookingSafetyEventType getType() {
        return type;
    }

    public void setType(BookingSafetyEventType type) {
        this.type = type;
    }

    public SafetyEventSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(SafetyEventSeverity severity) {
        this.severity = severity;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
