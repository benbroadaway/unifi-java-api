package com.benbroadaway.unifi;

import com.walmartlabs.concord.runtime.v2.sdk.MapBackedVariables;
import com.walmartlabs.concord.runtime.v2.sdk.Variables;

import java.util.HashMap;
import java.util.Map;

public class TaskParamsImpl implements TaskParams {

    protected final Variables input;

    public static TaskParams of(Map<String, Object> input, Map<String, Object> defaults, Map<String, Object> policyDefaults) {
        Map<String, Object> merged = new HashMap<>(policyDefaults);
        merged.putAll(defaults);
        merged.putAll(input);
        Variables vars = new MapBackedVariables(merged);

        Action action = Action.of(input.get("action").toString());

        switch (action) {
            case SET_USP_STATE:
                return new UspParamsImpl(vars);
            case NONE:
            default:
                throw new RuntimeException("Invalid action: " + action);
        }
    }

    protected TaskParamsImpl(Variables input) {
        this.input = input;
    }

    @Override
    public String action() {
        return input.assertString("action");
    }

    @Override
    public String unifiHost() {
        return input.assertString("unifiHost");
    }

    @Override
    public boolean validateCerts() {
        return input.getBoolean("validateCerts", true);
    }

    @Override
    public String username() {
        return input.assertString("username");
    }

    @Override
    public char[] password() {
        return input.assertString("password").toCharArray();
    }

    private static class UspParamsImpl extends TaskParamsImpl implements UspParams {

        public UspParamsImpl(Variables input) {
            super(input);
        }

        @Override
        public String uspName() {
            return input.assertString("uspName");
        }

        @Override
        public boolean relayState() {
            return input.assertBoolean("relayState");
        }
    }
}
