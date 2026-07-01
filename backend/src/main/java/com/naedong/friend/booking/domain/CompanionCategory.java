package com.naedong.friend.booking.domain;

import com.naedong.friend.common.PolicyViolationException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum CompanionCategory {
    COFFEE_BUDDY,
    MUSEUM_EXHIBITION_BUDDY,
    CITY_WALK_BUDDY,
    LANGUAGE_PRACTICE_BUDDY,
    SAFE_TRADE_BUDDY,
    ADMIN_APPOINTMENT_BUDDY;

    private static final Set<String> ALLOWED_NAMES = Arrays.stream(values())
            .map(Enum::name)
            .collect(Collectors.toUnmodifiableSet());

    public static CompanionCategory fromAllowedName(String categoryName) {
        if (categoryName == null || !ALLOWED_NAMES.contains(categoryName)) {
            throw new PolicyViolationException("Booking category is not allowed for the safety-first MVP: " + categoryName);
        }
        return CompanionCategory.valueOf(categoryName);
    }
}
