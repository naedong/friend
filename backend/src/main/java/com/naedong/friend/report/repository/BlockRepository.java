package com.naedong.friend.report.repository;

import com.naedong.friend.report.domain.Block;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlockRepository extends JpaRepository<Block, UUID> {

    boolean existsByBlockerIdAndBlockedUserId(UUID blockerId, UUID blockedUserId);
}
