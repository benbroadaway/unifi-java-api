package org.benbroadaway.unifi.cli;

import org.benbroadaway.unifi.actions.UnifiResult;
import org.benbroadaway.unifi.actions.Util;
import org.benbroadaway.unifi.cli.completion.BooleanCandidates;
import org.benbroadaway.unifi.client.ApiCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.Callable;

import static picocli.CommandLine.*;

@Command(name = "device",
        description = "Interact with Unifi devices",
        subcommands = { UspState.class })
public class Device implements Callable<UnifiResult<Void>> {
    private static final Logger log = LoggerFactory.getLogger(Device.class);
    @Option(names = {"-c", "--unifi-controller"},
            scope = ScopeType.INHERIT,
            description = "Unifi controller URL")
    String unifiHost = "https://192.168.1.1";

    @Option(names = {"--validate-certs"},
            scope = ScopeType.INHERIT,
            arity = "1",
            completionCandidates = BooleanCandidates.class,
            description = "Validate certificates. Candidates: ${COMPLETION-CANDIDATES}")
    Boolean validateCerts = true;

    @Option(names = {"--credentials-var"},
            scope = ScopeType.INHERIT,
            description = "Specify environment variable holding admin credentials formatted as <username>:<password>")
    String credentialVar = "UNIFI_CREDENTIALS";

    @Option(names = {"--credentials-file"}, scope = ScopeType.INHERIT,
            description = "Credentials file with username/password in JSON format")
    Path credentialsFile = Paths.get(System.getProperty("user.home"))
            .resolve(".unifi/auth/default.json");

    @ArgGroup(exclusive = false)
    CliCredentials cliCredentials;

    static class CliCredentials {
        @Option(names = {"--username"}, arity = "0..1", interactive = true, required = true,
                description = "Optional, prompts for username")
        String username;
        @Option(names = {"--password"}, arity = "0..1", interactive = true, required = true,
                description = "Optional, prompts for password")
        char[] password;
    }

    @Override
    public UnifiResult<Void> call() {
        return UnifiResult.success();
    }

    public ApiCredentials getCredentials() {
        return getCliCredentials()
                .or(this::getEnvCredentials)
                .or(this::getFileCredentials)
                .orElseThrow(() -> new IllegalArgumentException("Credentials must be supplied via cli, env, or file. See help for info."));
    }

    private Optional<ApiCredentials> getFileCredentials() {
        if (Files.exists(credentialsFile)) {
            return Optional.empty();
        }

        try (var is = Files.newInputStream(credentialsFile)) {
            return Optional.of(Util.getMapper().readValue(is, ApiCredentials.class));
        } catch (IOException e) {
            log.warn("Error reading credentials from '{}': {}", credentialsFile, e.getMessage());
        }

        return Optional.empty();
    }

    private Optional<ApiCredentials> getEnvCredentials() {
        return Optional.ofNullable(System.getenv(credentialVar))
                .map(String::toCharArray)
                .map(ApiCredentials::from);
    }

    private Optional<ApiCredentials> getCliCredentials() {
        if (cliCredentials.username == null) {
            return Optional.empty();
        }

        return Optional.of(ApiCredentials.getInstance(cliCredentials.username, cliCredentials.password));
    }
}
