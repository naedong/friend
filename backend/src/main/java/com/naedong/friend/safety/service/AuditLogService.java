package com.naedong.friend.safety.service;

import com.naedong.friend.common.RequestMetadata;
import com.naedong.friend.safety.domain.AuditLog;
import com.naedong.friend.safety.repository.AuditLogRepository;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    public AuditLog record(
            UUID actorUserId,
            String action,
            String targetType,
            UUID targetId,
            String reason,
            RequestMetadata metadata
    ) {
        AuditLog auditLog = new AuditLog();
        auditLog.setActorUserId(actorUserId);
        auditLog.setAction(action);
        auditLog.setTargetType(targetType);
        auditLog.setTargetId(targetId);
        auditLog.setReason(reason);
        auditLog.setIpHash(hashNullable(metadata == null ? null : metadata.ipAddress()));
        auditLog.setUserAgentHash(hashNullable(metadata == null ? null : metadata.userAgent()));
        return auditLogRepository.save(auditLog);
    }

    @Transactional
    public AuditLog recordSystem(String action, String targetType, UUID targetId, String reason) {
        return record(null, action, targetType, targetId, reason, RequestMetadata.empty());
    }

    private String hashNullable(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return null;
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(rawValue.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is required for audit metadata hashing", exception);
        }
    }
}
