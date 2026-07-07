package com.naedong.friend.safety.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.naedong.friend.common.RequestMetadata;
import com.naedong.friend.safety.domain.AuditLog;
import com.naedong.friend.safety.repository.AuditLogRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuditLogServiceHashingTest {

    @Mock
    AuditLogRepository auditLogRepository;

    @BeforeEach
    void setUp() {
        when(auditLogRepository.save(any(AuditLog.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void metadataHashingIsDeterministicWithSamePepper() {
        AuditLogService service = new AuditLogService(auditLogRepository, "pepper-one");

        AuditLog first = service.record(UUID.randomUUID(), "ACTION", "TARGET", UUID.randomUUID(), "reason", metadata());
        AuditLog second = service.record(UUID.randomUUID(), "ACTION", "TARGET", UUID.randomUUID(), "reason", metadata());

        assertThat(first.getIpHash()).isEqualTo(second.getIpHash());
        assertThat(first.getUserAgentHash()).isEqualTo(second.getUserAgentHash());
    }

    @Test
    void metadataHashingChangesWithDifferentPepper() {
        AuditLogService firstService = new AuditLogService(auditLogRepository, "pepper-one");
        AuditLogService secondService = new AuditLogService(auditLogRepository, "pepper-two");

        AuditLog first = firstService.record(UUID.randomUUID(), "ACTION", "TARGET", UUID.randomUUID(), "reason", metadata());
        AuditLog second = secondService.record(UUID.randomUUID(), "ACTION", "TARGET", UUID.randomUUID(), "reason", metadata());

        assertThat(first.getIpHash()).isNotEqualTo(second.getIpHash());
        assertThat(first.getUserAgentHash()).isNotEqualTo(second.getUserAgentHash());
    }

    @Test
    void metadataHashingDoesNotStoreRawNetworkMetadata() {
        AuditLogService service = new AuditLogService(auditLogRepository, "pepper-one");
        RequestMetadata metadata = metadata();

        AuditLog auditLog = service.record(UUID.randomUUID(), "ACTION", "TARGET", UUID.randomUUID(), "reason", metadata);

        assertThat(auditLog.getIpHash()).isNotEqualTo(metadata.ipAddress());
        assertThat(auditLog.getUserAgentHash()).isNotEqualTo(metadata.userAgent());
        assertThat(auditLog.getIpHash()).hasSize(64);
        assertThat(auditLog.getUserAgentHash()).hasSize(64);
    }

    private RequestMetadata metadata() {
        return new RequestMetadata("203.0.113.10", "JUnit User Agent");
    }
}
