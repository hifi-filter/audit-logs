package com.hififilter.audit.logs.server.runtime;

import com.hififilter.audit.logs.common.runtime.AuditLogsConfig;
import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import lombok.Getter;

/**
 * Audit logs extension configuration
 */
@ConfigRoot(name = "hifi-filter.audit-logs", phase = ConfigPhase.RUN_TIME)
public class AuditLogsServerConfig {

    /**
     * Server configuration
     */
    @Getter
    @ConfigItem(name = ConfigItem.PARENT)
    protected AuditLogsConfig server;
}
