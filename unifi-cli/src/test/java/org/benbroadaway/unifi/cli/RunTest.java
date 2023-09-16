package org.benbroadaway.unifi.cli;

import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class RunTest extends AbstractTest {

    @TempDir
    private Path tempDir;



    private static int run(List<String> args) throws Exception {
        return run(args, null);
    }

    private static int run(List<String> args, String defaultCfg) throws Exception {
        App app = new App();
        CommandLine cmd = new CommandLine(app);

        List<String> effectiveArgs = new ArrayList<>();
        effectiveArgs.add("run");
        effectiveArgs.addAll(args);

//        if (defaultCfg != null) {
//            effectiveArgs.add("--default-cfg");
//            effectiveArgs.add(dst.path().resolve(defaultCfg).toString());
//        }

        return cmd.execute(effectiveArgs.toArray(new String[0]));
    }
}
