package com.naedong.friend.safety.domain;

import com.naedong.friend.common.CreatedAtEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "safety_card")
public class SafetyCard extends CreatedAtEntity {

    @Column(nullable = false, unique = true)
    private UUID bookingId;

    @Column(nullable = false, unique = true)
    private String publicToken;

    @Column(nullable = false)
    private Instant expiresAt;

    public UUID getBookingId() {
        return bookingId;
    }

    public void setBookingId(UUID bookingId) {
        this.bookingId = bookingId;
    }

    public String getPublicToken() {
        return publicToken;
    }

    public void setPublicToken(String publicToken) {
        this.publicToken = publicToken;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }
}
