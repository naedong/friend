package com.naedong.friend.user.domain;

import com.naedong.friend.common.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_verification")
public class UserVerification extends AuditableEntity {

    @Column(nullable = false, unique = true)
    private UUID userId;

    @Column(nullable = false)
    private boolean emailVerified;

    @Column(nullable = false)
    private boolean phoneVerified;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VerificationStatus identityStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VerificationStatus livenessStatus;

    private String providerName;

    private String providerReferenceId;

    private Instant verifiedAt;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public boolean isPhoneVerified() {
        return phoneVerified;
    }

    public void setPhoneVerified(boolean phoneVerified) {
        this.phoneVerified = phoneVerified;
    }

    public VerificationStatus getIdentityStatus() {
        return identityStatus;
    }

    public void setIdentityStatus(VerificationStatus identityStatus) {
        this.identityStatus = identityStatus;
    }

    public VerificationStatus getLivenessStatus() {
        return livenessStatus;
    }

    public void setLivenessStatus(VerificationStatus livenessStatus) {
        this.livenessStatus = livenessStatus;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getProviderReferenceId() {
        return providerReferenceId;
    }

    public void setProviderReferenceId(String providerReferenceId) {
        this.providerReferenceId = providerReferenceId;
    }

    public Instant getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(Instant verifiedAt) {
        this.verifiedAt = verifiedAt;
    }
}
