package com.hififilter.audit.logs.server.runtime;

import io.quarkus.runtime.annotations.Recorder;

/**
 * Audit logs server recorder
 */
@Recorder
public class AuditLogsRecorder {

    /**
     * Initialize audit log handler
     *
     * @param config Audit logs extension runtime config
     * @throws IllegalArgumentException If audit logs are enabled but endpoint is not set
     */
    public void initialize(final AuditLogsServerConfig config) {
        if (config.server().enabled() && config.server().endpoint().isEmpty()) {
            throw new IllegalArgumentException(
                "Audit-logs are enabled so the property quarkus.hifi-filter.audit-logs.endpoint is mandatory"
            );
        }
    }
}
