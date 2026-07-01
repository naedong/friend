package com.naedong.friend.report.domain;

import com.naedong.friend.common.CreatedAtEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "user_block")
public class Block extends CreatedAtEntity {

    @Column(nullable = false)
    private UUID blockerId;

    @Column(nullable = false)
    private UUID blockedUserId;

    public UUID getBlockerId() {
        return blockerId;
    }

    public void setBlockerId(UUID blockerId) {
        this.blockerId = blockerId;
    }

    public UUID getBlockedUserId() {
        return blockedUserId;
    }

    public void setBlockedUserId(UUID blockedUserId) {
        this.blockedUserId = blockedUserId;
    }
}
