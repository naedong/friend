package com.naedong.friend.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class BackendUnsafePatternTest {

    private static final Path SOURCE_ROOT = Path.of("src", "main", "java");

    private static final List<String> BANNED_PATTERNS = List.of(
            "Runtime.getRuntime()." + "exec",
            "new " + "ProcessBuilder",
            "createNativeQuery(",
            "@Query(\" +",
            "java.sql." + "Statement",
            "executeQuery(\"",
            "executeUpdate(\"",
            "System.out.",
            "System.err.",
            ".printStackTrace("
    );

    @Test
    void backendSourceDoesNotUseObviousUnsafeShellQueryOrConsoleLoggingPatterns() throws IOException {
        try (Stream<Path> sourceFiles = Files.walk(SOURCE_ROOT)) {
            List<String> violations = sourceFiles
                    .filter(path -> path.toString().endsWith(".java"))
                    .flatMap(BackendUnsafePatternTest::violationsIn)
                    .toList();

            assertThat(violations).isEmpty();
        }
    }

    private static Stream<String> violationsIn(Path path) {
        try {
            String source = Files.readString(path);
            return BANNED_PATTERNS.stream()
                    .filter(source::contains)
                    .map(pattern -> path + " contains banned pattern: " + pattern);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to read source file: " + path, exception);
        }
    }
}
