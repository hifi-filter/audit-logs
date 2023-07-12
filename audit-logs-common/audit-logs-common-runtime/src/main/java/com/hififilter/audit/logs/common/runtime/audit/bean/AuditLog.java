package com.hififilter.audit.logs.common.runtime.audit.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.lang.reflect.Method;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Model used to generate audit log and transform it into JSON
 */
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuditLog {

    /**
     * Audit log action (human-readable string to group audit logs)
     */
    @Setter
    protected String action = "";

    /**
     * Method invoked (method resource in JAX-RS context, Picocli command in exec context)
     */
    @Setter
    protected Method invokedMethod;

    /**
     * Random UUID
     */
    @Getter
    @JsonProperty
    protected String uuid = UUID.randomUUID().toString();

    /**
     * Start datetime
     */
    @Getter
    @JsonProperty
    protected LocalDateTime startDatetime = LocalDateTime.now(Clock.systemUTC());

    /**
     * End datetime
     */
    @Getter
    @Setter
    @JsonProperty
    protected LocalDateTime endDatetime;

    /**
     * User information
     */
    @Getter
    @JsonProperty
    protected AuditLogUser user = new AuditLogUser();

    /**
     * Custom fields to add in audit log
     */
    protected Map<String, Object> customFields = new HashMap<>();

    /**
     * Input of invoked method (parameters, stdin or http request for example)
     */
    @Getter
    @JsonProperty
    protected AuditLogHttpRequest request = new AuditLogHttpRequest();

    /**
     * Output of invoked method (rc/stdout or http response for example)
     */
    @Getter
    @JsonProperty
    protected AuditLogHttpResponse response = new AuditLogHttpResponse();

    /**
     * Inject custom field
     *
     * @param key Key
     * @param value Value
     */
    public void putCustomField(final String key, final Object value) {
        customFields.put(key, value);
    }

    /**
     * Set endDatetime as now
     */
    public void setEndDatetimeAsNow() {
        endDatetime = LocalDateTime.now(Clock.systemUTC());
    }

    /**
     * Action name. Null if action is not defined (empty string)
     *
     * @return Action name. Null if action is not defined (empty string)
     */
    @JsonProperty
    public String action() {
        return action == null || action.isBlank() ? null : action;
    }

    /**
     * Custom fields. Null if empty to avoid JSON serialization
     *
     * @return Custom fields. Null if empty to avoid JSON serialization
     */
    @JsonProperty
    public Map<String, Object> customFields() {
        return customFields.isEmpty() ? null : customFields;
    }

    /**
     * Request duration. Computed from start and end datetime
     *
     * @return Duration of request
     */
    @JsonProperty
    public Long durationInMs() {
        if (startDatetime == null || endDatetime == null) {
            return null;
        }
        return Duration.between(startDatetime, endDatetime).toMillis();
    }

    /**
     * Returns signature of invoked method as string
     *
     * @return signature of invoked method as string
     */
    @JsonProperty
    public String invokedMethodSignature() {
        return invokedMethod == null
            ? null
            : invokedMethod.getDeclaringClass().getCanonicalName() + "." + invokedMethod.getName();
    }

}
