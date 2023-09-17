package org.benbroadaway.unifi.cli;

import org.junit.jupiter.api.Test;

import java.util.List;

class MainTest extends AbstractTest {
    @Test
    void testMain() {
        var args = List.of("--help");

        Main.main(args.toArray(new String[0]));

        assertLog(".*Usage:.*");
    }
}
