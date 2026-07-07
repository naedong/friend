package com.naedong.friend.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public final class AiInputSanitizer {

    public static final int DEFAULT_MAX_LENGTH = 2_000;

    private static final List<Pattern> DANGEROUS_PATTERNS = List.of(
            Pattern.compile("\\bignore\\s+(all\\s+)?previous\\s+instructions\\b"),
            Pattern.compile("\\bsystem\\s+prompt\\b"),
            Pattern.compile("\\bdeveloper\\s+message\\b"),
            Pattern.compile("\\bact\\s+as\\s+admin\\b"),
            Pattern.compile("\\brun\\s+shell\\b"),
            Pattern.compile("\\bexecute\\s+command\\b"),
            Pattern.compile("\\bdrop\\s+table\\b"),
            Pattern.compile("\\bdelete\\s+all\\b"),
            Pattern.compile("\\bbypass\\s+safety\\b"),
            Pattern.compile("\\bdisable\\s+policy\\b")
    );

    public SanitizedAiInput sanitize(String rawInput) {
        return sanitize(rawInput, DEFAULT_MAX_LENGTH);
    }

    public SanitizedAiInput sanitize(String rawInput, int maxLength) {
        if (rawInput == null) {
            return new SanitizedAiInput("", false, List.of(Violation.NULL_INPUT));
        }

        String trimmed = rawInput.trim();
        List<Violation> violations = new ArrayList<>();
        if (trimmed.length() > maxLength) {
            violations.add(Violation.TOO_LONG);
        }
        if (trimmed.indexOf('\0') >= 0) {
            violations.add(Violation.NULL_BYTE);
        }
        if (containsUnsafeControlCharacter(trimmed)) {
            violations.add(Violation.CONTROL_CHARACTER);
        }
        if (containsDangerousPattern(trimmed)) {
            violations.add(Violation.DANGEROUS_PATTERN);
        }

        return new SanitizedAiInput(trimmed, violations.isEmpty(), List.copyOf(violations));
    }

    private boolean containsUnsafeControlCharacter(String value) {
        for (int i = 0; i < value.length(); i++) {
            char current = value.charAt(i);
            if (Character.isISOControl(current)
                    && current != '\n'
                    && current != '\r'
                    && current != '\t') {
                return true;
            }
        }
        return false;
    }

    private boolean containsDangerousPattern(String value) {
        String normalized = value.toLowerCase(Locale.ROOT);
        return DANGEROUS_PATTERNS.stream()
                .anyMatch(pattern -> pattern.matcher(normalized).find());
    }

    public record SanitizedAiInput(String value, boolean accepted, List<Violation> violations) {
        public boolean hasViolation(Violation violation) {
            return violations.contains(violation);
        }
    }

    public enum Violation {
        NULL_INPUT,
        TOO_LONG,
        NULL_BYTE,
        CONTROL_CHARACTER,
        DANGEROUS_PATTERN
    }
}
