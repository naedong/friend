package com.naedong.friend.ai;

import java.util.EnumSet;
import java.util.Set;

public final class AiInputPolicy {

    private static final Set<AiSuggestedActionType> PRIVILEGED_ACTIONS = EnumSet.of(
            AiSuggestedActionType.BOOKING_STATE_TRANSITION,
            AiSuggestedActionType.SAFETY_ESCALATION,
            AiSuggestedActionType.PAYMENT_ACTION,
            AiSuggestedActionType.KYC_ACTION,
            AiSuggestedActionType.MODERATOR_ACTION,
            AiSuggestedActionType.ADMIN_ACTION
    );

    private AiInputPolicy() {
    }

    public static boolean isAiOutputAdvisoryOnly(AiSuggestedActionType actionType) {
        return !PRIVILEGED_ACTIONS.contains(actionType);
    }

    public static boolean requiresDeterministicServerValidation(AiSuggestedActionType actionType) {
        return PRIVILEGED_ACTIONS.contains(actionType);
    }

    public enum AiSuggestedActionType {
        TEXT_SUMMARY,
        USER_SUPPORT_DRAFT,
        BOOKING_STATE_TRANSITION,
        SAFETY_ESCALATION,
        PAYMENT_ACTION,
        KYC_ACTION,
        MODERATOR_ACTION,
        ADMIN_ACTION
    }
}
