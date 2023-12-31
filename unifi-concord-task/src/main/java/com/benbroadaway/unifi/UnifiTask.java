package com.benbroadaway.unifi;

import com.walmartlabs.concord.runtime.v2.sdk.Context;
import com.walmartlabs.concord.runtime.v2.sdk.Task;
import com.walmartlabs.concord.runtime.v2.sdk.TaskResult;
import com.walmartlabs.concord.runtime.v2.sdk.Variables;
import org.benbroadaway.unifi.actions.ActionResult;
import org.benbroadaway.unifi.actions.usp.SetRelayState;
import org.benbroadaway.unifi.client.ApiCredentials;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

@Named("unifi")
public class UnifiTask implements Task {
    private final Map<String, Object> globalDefaults;
    private final Map<String, Object> policyDefaults;

    private static final Map<Action, Function<TaskParams, TaskResult>> handlers;

    static {
        handlers = new EnumMap<>(Action.class);
        handlers.put(Action.SET_USP_STATE, params -> setUspState((TaskParams.UspParams) params));
    }

    private static final Function<TaskParams, TaskResult> defaultHandler = p -> TaskResult.fail("Unknown action '" + p.action() + "'");

    @Inject
    public UnifiTask(Context ctx) {
        this.globalDefaults = ctx.variables().getMap("UnifiTaskDefaults", Collections.emptyMap());
        this.policyDefaults = ctx.defaultVariables().toMap();
    }

    @Override
    public TaskResult execute(Variables input) {
        TaskParams params = TaskParamsImpl.of(input.toMap(), globalDefaults, policyDefaults);
        Action action = Action.of(params.action());

        return handlers.getOrDefault(action, defaultHandler).apply(params);
    }

    private static TaskResult setUspState(TaskParams.UspParams params) {
        int index = params.outletIndex()
                .orElseThrow(() -> new IllegalArgumentException("Input parameter 'outletIndex' is required!"));


        SetRelayState setRelayState = SetRelayState.getInstance(params.unifiHost(),
                params.uspName(), index, getCredentials(params), params.validateCerts(), params.relayState());

        ActionResult<Void> result = setRelayState.call();

        if (!result.ok()) {
            return TaskResult.fail(new IllegalStateException("Failed to set state: " + result.error()));
        }

        return TaskResult.success();
    }

    private static ApiCredentials getCredentials(TaskParams params) {
        return ApiCredentials.getInstance(params.username(), params.password());
    }
}
