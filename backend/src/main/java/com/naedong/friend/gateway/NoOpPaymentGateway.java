package com.naedong.friend.gateway;

import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({"dev", "test"})
public class NoOpPaymentGateway implements PaymentGateway {

    @Override
    public void freezePayout(UUID bookingId, String reason) {
        // Local development only. Real payout holds must be provider-backed and audited.
    }
}
