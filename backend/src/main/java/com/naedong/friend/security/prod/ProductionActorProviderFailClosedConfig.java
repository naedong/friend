package com.naedong.friend.security.prod;

import com.naedong.friend.security.ActorProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!dev & !test")
public class ProductionActorProviderFailClosedConfig {

    @Bean
    @ConditionalOnMissingBean(ActorProvider.class)
    ActorProvider failClosedProductionActorProvider() {
        throw new IllegalStateException(FailClosedProductionActorProvider.MESSAGE);
    }
}
