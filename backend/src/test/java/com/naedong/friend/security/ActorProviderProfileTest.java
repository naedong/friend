package com.naedong.friend.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.naedong.friend.security.dev.DevelopmentActorProvider;
import com.naedong.friend.security.prod.FailClosedProductionActorProvider;
import com.naedong.friend.security.prod.ProductionActorProviderFailClosedConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

class ActorProviderProfileTest {

    @Test
    void devActorCannotBeEnabledOutsideDevOrTestProfiles() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            TestPropertyValues.of("friend.security.dev-actor-enabled=true").applyTo(context);
            context.register(DevelopmentActorProvider.class);

            context.refresh();

            assertThat(context.getBeansOfType(DevelopmentActorProvider.class)).isEmpty();
        }
    }

    @Test
    void productionProfileFailsClosedWithoutRealActorProvider() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.register(ProductionActorProviderFailClosedConfig.class);

            assertThatThrownBy(context::refresh)
                    .hasRootCauseInstanceOf(IllegalStateException.class)
                    .hasRootCauseMessage("No production ActorProvider is configured. Do not start Friend outside dev/test without real authentication.");
        }
    }
}
