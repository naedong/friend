package com.naedong.friend.user.repository;

import com.naedong.friend.user.domain.TrustedContact;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrustedContactRepository extends JpaRepository<TrustedContact, UUID> {

    List<TrustedContact> findByUserIdAndActiveTrue(UUID userId);
}
