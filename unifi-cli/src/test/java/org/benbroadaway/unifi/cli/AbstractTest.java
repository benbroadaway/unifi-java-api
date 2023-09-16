package org.benbroadaway.unifi.cli;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public abstract class AbstractTest {
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private final ByteArrayOutputStream out = new ByteArrayOutputStream();
    private final ByteArrayOutputStream err = new ByteArrayOutputStream();

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
        String outStr = out.toString();
        if (grep(outStr, pattern) != 1) {
            fail("Expected a single log entry: '" + pattern + "', got: \n" + outStr);
        }
    }

    protected void assertLog(String pattern, int times) {
        String outStr = out.toString();
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

    private static int grep(String str, String pattern) {
        int cnt = 0;

        String[] lines = str.split("\\r?\\n");
        for (String line : lines) {
            if (line.matches(pattern)) {
                cnt++;
            }
        }

        return cnt;
    }
}
