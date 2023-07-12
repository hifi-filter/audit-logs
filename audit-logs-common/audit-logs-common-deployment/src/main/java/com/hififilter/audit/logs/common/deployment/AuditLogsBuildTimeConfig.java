package com.hififilter.audit.logs.common.deployment;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

/**
 * Audit logs extension build time configuration
 */
@ConfigRoot(name = "hifi-filter.audit-logs", phase = ConfigPhase.BUILD_TIME)
public class AuditLogsBuildTimeConfig {

    /**
     * Extension metrics configuration
     */
    @ConfigItem
    protected AuditLogsMetricsConfig metrics;
}
