# Unifi Java Libraries

Collection of Java libraries for interacting with Ubiquiti Unifi devices.

## Current state

Very new. Likely breaking changes with subsequent releases. Current functionality
is limited to controlling single-plug USP devices.

## TODOs

- [X] verbose/debug option for more logging
- [ ] generate autocompletion with build/release
- [ ] support for multi-plug USP devices
- [X] reorganize command hierarchy to make flags more intuitive (especially for autocompletion)
- [ ] support for more devices (ap, etc)
- [ ] generic device support?
    - The (undocumented) REST API returns a _lot_ of
      overlapping, and not overlapping, attributes for devices
      which makes keeping things strongly typed here a bit of a
      headache.

## Requirements

- Maven 3+
- Java JDK 17

## Building

```shell
# compile, test, package
$ mvn clean install
```

```shell
# deploy snapshot
$ export ARTIFACT_REPOSITORY_SNAPSHOTS=https://...
$ mvn deploy
```

```shell
# prepare release
$ mvn release:prepare
# set version info
$ export ARTIFACT_REPOSITORY_RELEASES=https://...
$ mvn release:perform
```

## Use the CLI

```shell
$ export UNIFI_CLI_VERSION=0.0.4 # customize, use snapshot
# give execution permission
$ chmod +x ~/.m2/repository/org/benbroadaway/unifi/unifi-cli/${UNIFI_CLI_VERSION}/unifi-cli-${UNIFI_CLI_VERSION}-executable.jar
# create alias
$ alias unifi=~/.m2/repository/org/benbroadaway/unifi/unifi-cli/${UNIFI_CLI_VERSION}/unifi-cli-${UNIFI_CLI_VERSION}-executable.jar

$ unifi --version
0.0.4
$ unifi device --help
Usage: unifi device [--validate-certs] [--credentials-file=<credentialsFile>]
                    [--credentials-var=<credentialVar>] [-u=<unifiHost>]
                    [--username[=<username>] --password[=<password>]] [COMMAND]
Interact with Unifi devices
      --credentials-file=<credentialsFile>
                         Credentials file with username/password in JSON format
      --credentials-var=<credentialVar>
                         Specify environment variable holding admin credentials
                           formatted as <username>:<password>
      --password[=<password>]
                         Optional, prompts for password
  -u, --unifi-host=<unifiHost>
                         Unifi controller URL
      --username[=<username>]
                         Optional, prompts for username
      --validate-certs   Validate certificates
Commands:
  usp-state   Get USP relay state
  usp-toggle  Set USP relay state
```

### CLI command completion

Nothing too different from the main docs: https://picocli.info/autocomplete.html

```shell
$ export UNIFI_CLI_VERSION=0.0.4
$ java -cp ~/.m2/repository/org/benbroadaway/unifi/unifi-cli/${UNIFI_CLI_VERSION}/unifi-cli-${UNIFI_CLI_VERSION}-executable.jar \
    picocli.AutoComplete \
    -n unifi \
    org.benbroadaway.unifi.cli.App
# ...creates unifi_completion file

$ source unifi_completion
$ unifi device --<tab>
# shows hints
--credentials-var    --credentials-file  --password          --unifi-host        --username
```
