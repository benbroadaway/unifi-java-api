package com.benbroadaway.unifi;

public enum Action {
    SET_USP_STATE("setUspState"),
    NONE("none");


    private final String value;

    Action(String value) {
        this.value = value;
    }

    public static Action of(String fromValue) {
        for (Action a : Action.values()) {
            if (a.value.equalsIgnoreCase(fromValue)) {
                return a;
            }
        }

        return NONE;
    }
}
