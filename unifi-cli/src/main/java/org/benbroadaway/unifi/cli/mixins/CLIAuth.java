package org.benbroadaway.unifi.cli.mixins;

import org.benbroadaway.unifi.client.ApiCredentials;

import java.util.Optional;

import static picocli.CommandLine.*;

@Command(synopsisHeading      = "%nUsage:%n%n",
        descriptionHeading   = "%nDescription:%n%n",
        parameterListHeading = "%nParameters:%n%n",
        optionListHeading    = "%nOptions:%n%n",
        commandListHeading   = "%nCommands:%n%n")
public class CLIAuth {

    @ArgGroup(exclusive = false)
    private CliCredentials cliCredentials;

    public static class CliCredentials {
        @Option(names = {"--username"}, arity = "0..1", interactive = true, required = true,
                description = "Optional, prompts for username")
        String username;
        @Option(names = {"--password"}, arity = "0..1", interactive = true, required = true,
                description = "Optional, prompts for password")
        char[] password;
    }

    public Optional<ApiCredentials> getCliCredentials() {
        if (cliCredentials == null) {
            return Optional.empty();
        }

        return Optional.of(ApiCredentials.getInstance(cliCredentials.username, cliCredentials.password));
    }
}
