package com.naedong.friend.safety.service;

import com.naedong.friend.common.RequestMetadata;
import com.naedong.friend.safety.domain.AuditLog;
import com.naedong.friend.safety.repository.AuditLogRepository;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditLogService {

    private static final Logger log = LoggerFactory.getLogger(AuditLogService.class);
    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final String DEVELOPMENT_HASH_PEPPER = "friend-development-audit-hash-pepper-not-for-production";

    private final AuditLogRepository auditLogRepository;
    private final SecretKeySpec hashKey;

    public AuditLogService(
            AuditLogRepository auditLogRepository,
            @Value("${friend.audit.hash-pepper:}") String configuredHashPepper,
            Environment environment
    ) {
        this(auditLogRepository, resolveHashPepper(configuredHashPepper, environment));
    }

    AuditLogService(AuditLogRepository auditLogRepository, String hashPepper) {
        if (hashPepper == null || hashPepper.isBlank()) {
            throw new IllegalStateException("Audit hash pepper must not be blank.");
        }
        this.auditLogRepository = auditLogRepository;
        this.hashKey = new SecretKeySpec(hashPepper.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM);
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
        return record(null, action, targetType, targetId, "SYSTEM_TRIGGERED: " + reason, RequestMetadata.empty());
    }

    private String hashNullable(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return null;
        }
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(hashKey);
            return HexFormat.of().formatHex(mac.doFinal(rawValue.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException | InvalidKeyException exception) {
            throw new IllegalStateException(HMAC_ALGORITHM + " is required for audit metadata hashing", exception);
        }
    }

    private static String resolveHashPepper(String configuredHashPepper, Environment environment) {
        if (configuredHashPepper != null && !configuredHashPepper.isBlank()) {
            return configuredHashPepper;
        }
        if (hasActiveProfile(environment, "dev") || hasActiveProfile(environment, "test")) {
            log.warn("Using development audit hash pepper. Configure friend.audit.hash-pepper outside local dev/test.");
            return DEVELOPMENT_HASH_PEPPER;
        }
        throw new IllegalStateException("friend.audit.hash-pepper is required outside dev/test profiles.");
    }

    private static boolean hasActiveProfile(Environment environment, String profile) {
        for (String activeProfile : environment.getActiveProfiles()) {
            if (profile.equals(activeProfile)) {
                return true;
            }
        }
        return false;
    }
}
