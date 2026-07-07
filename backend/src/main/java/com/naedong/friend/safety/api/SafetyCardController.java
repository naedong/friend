package com.naedong.friend.safety.api;

import com.naedong.friend.safety.service.SafetyCardService;
import com.naedong.friend.safety.service.SafetyCardToken;
import com.naedong.friend.safety.service.SafetyCardView;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/safety-cards")
public class SafetyCardController {

    private final SafetyCardService safetyCardService;

    public SafetyCardController(SafetyCardService safetyCardService) {
        this.safetyCardService = safetyCardService;
    }

    @GetMapping("/{token}")
    public SafetyCardView getSafetyCard(
            @PathVariable @Pattern(regexp = SafetyCardToken.PUBLIC_TOKEN_PATTERN, message = "invalid safety card token") String token
    ) {
        return safetyCardService.getSafetyCardView(token);
    }
}
