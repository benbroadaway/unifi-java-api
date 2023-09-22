package org.benbroadaway.unifi.cli;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public abstract class AbstractTest {
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private final ByteArrayOutputStream out = new ByteArrayOutputStream();
    private final ByteArrayOutputStream err = new ByteArrayOutputStream();

    @TempDir
    protected Path tempDir;

    @BeforeEach
    public void setUpStreams() {
        out.reset();
        err.reset();
        System.setOut(new PrintStream(out));
        System.setErr(new PrintStream(err));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    protected void assertLog(String pattern) {
        var outStr = out.toString();
        if (grep(outStr, pattern) != 1) {
            fail("Expected a single log entry: '" + pattern + "', got: \n" + outStr);
        }
    }

    protected void assertLogErr(String pattern) {
        var outStr = err.toString();
        if (grep(outStr, pattern) != 1) {
            fail("Expected a single err log entry: '" + pattern + "', got: \n" + outStr);
        }
    }

    protected void assertLog(String pattern, int times) {
        var outStr = out.toString();
        if (grep(outStr, pattern) != times) {
            fail("Expected a single log entry: '" + pattern + "', got: \n" + outStr);
        }
    }

    protected String stdOut() {
        return out.toString();
    }

    protected String stdErr() {
        return err.toString();
    }

    protected void assertExitCode(int expected, int current) {
        assertEquals(expected, current, () -> "out:\n" + stdOut() + "\n\n" + "err:\n" + stdErr());
    }

    protected int run(List<String> args, Map<Class<?>, Object> mockCommands) {
        var app = new App();
        var cmd = new CommandLine(app, new MockFactory(mockCommands));

        var effectiveArgs = new ArrayList<>(args);

        return cmd.execute(effectiveArgs.toArray(new String[0]));
    }

    private static class MockFactory implements CommandLine.IFactory {
        private final Map<Class<?>, Object> mockClasses;

        public MockFactory(Map<Class<?>, Object> mockClasses) {
            this.mockClasses = mockClasses;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <K> K create(Class<K> cls) throws Exception {
            if (mockClasses.containsKey(cls)) {
                return (K) mockClasses.get(cls);
            }

            return cls.getDeclaredConstructor().newInstance();
        }
    }

    private static int grep(String str, String pattern) {
        int cnt = 0;

        var lines = str.split("\\r?\\n");
        for (var line : lines) {
            if (line.matches(pattern)) {
                cnt++;
            }
        }

        return cnt;
    }
}
