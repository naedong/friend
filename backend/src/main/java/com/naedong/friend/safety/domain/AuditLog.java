package com.naedong.friend.safety.domain;

import com.naedong.friend.common.CreatedAtEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "audit_log")
public class AuditLog extends CreatedAtEntity {

    private UUID actorUserId;

    @Column(nullable = false)
    private String action;

    @Column(nullable = false)
    private String targetType;

    @Column(nullable = false)
    private UUID targetId;

    @Column(length = 1_000)
    private String reason;

    private String ipHash;

    private String userAgentHash;

    public UUID getActorUserId() {
        return actorUserId;
    }

    public void setActorUserId(UUID actorUserId) {
        this.actorUserId = actorUserId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public UUID getTargetId() {
        return targetId;
    }

    public void setTargetId(UUID targetId) {
        this.targetId = targetId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getIpHash() {
        return ipHash;
    }

    public void setIpHash(String ipHash) {
        this.ipHash = ipHash;
    }

    public String getUserAgentHash() {
        return userAgentHash;
    }

    public void setUserAgentHash(String userAgentHash) {
        this.userAgentHash = userAgentHash;
    }
}
