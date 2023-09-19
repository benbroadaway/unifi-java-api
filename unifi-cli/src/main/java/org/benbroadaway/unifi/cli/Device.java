package org.benbroadaway.unifi.cli;

import org.benbroadaway.unifi.actions.Util;
import org.benbroadaway.unifi.cli.completion.BooleanCandidates;
import org.benbroadaway.unifi.cli.mixins.CLIAuth;
import org.benbroadaway.unifi.cli.mixins.Log;
import org.benbroadaway.unifi.client.ApiCredentials;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import static picocli.CommandLine.*;

@Command(name = "device",
        description = "Interact with Unifi devices",
        subcommands = { UspState.class })
public class Device implements Callable<Integer> {
    @Spec
    @SuppressWarnings("unused")
    private Model.CommandSpec spec;

    @Mixin
    protected Log log;

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

    @Override
    public Integer call() {
        log.warn("sub-command is missing");

        return 1;
    }

    public ApiCredentials getCredentials(CLIAuth cliAuth, Supplier<Model.CommandSpec> specSupplier) {
        return cliAuth.getCliCredentials()
                .or(this::getEnvCredentials)
                .or(this::getFileCredentials)
                .orElseThrow(() -> {
                    specSupplier.get().commandLine().usage(System.out);
                    return new IllegalArgumentException("Credentials must be supplied via cli, env, or file. See help for info.");
                });
    }

    private Optional<ApiCredentials> getFileCredentials() {
        if (!Files.exists(credentialsFile)) {
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
}
