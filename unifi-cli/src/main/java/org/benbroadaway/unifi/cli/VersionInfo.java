package org.benbroadaway.unifi.cli;

import java.io.IOException;
import java.util.Properties;

public class VersionInfo {

    private VersionInfo() {
    }

    private static final String VERSION;

    static {
        Properties props = new Properties();
        try {
            props.load(VersionInfo.class.getClassLoader().getResourceAsStream("project.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        VERSION = props.getProperty("project.version");
    }

    public static String getVersion() {
        return VERSION;
    }
}
