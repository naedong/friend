package com.naedong.friend.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

class DevelopmentCorsConfigurationTest {

    @Test
    void allowsLocalFlutterWebOriginsAndRejectsExternalOrigins() {
        CorsConfigurationSource source = new SecurityConfig().developmentCorsConfigurationSource();
        MockHttpServletRequest request = new MockHttpServletRequest("OPTIONS", "/api/booking-options");
        CorsConfiguration configuration = source.getCorsConfiguration(request);

        assertThat(configuration).isNotNull();
        assertThat(configuration.checkOrigin("http://localhost:5400"))
                .isEqualTo("http://localhost:5400");
        assertThat(configuration.checkOrigin("http://127.0.0.1:5400"))
                .isEqualTo("http://127.0.0.1:5400");
        assertThat(configuration.checkOrigin("https://attacker.example"))
                .isNull();
    }
}
