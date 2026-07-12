package com.naedong.friend.safety.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.naedong.friend.safety.repository.AuditLogRepository;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

class AuditLogServiceWiringTest {

    @Test
    void springSelectsTheConfigurationAwareConstructor() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.getEnvironment().setActiveProfiles("test");
            context.registerBean(AuditLogRepository.class, () -> mock(AuditLogRepository.class));
            context.register(AuditLogService.class);

            context.refresh();

            assertThat(context.getBean(AuditLogService.class)).isNotNull();
        }
    }
}
