package com.hififilter.audit.logs.common.deployment;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;

/**
 * Metrics configuration
 */
@ConfigGroup
public class AuditLogsMetricsConfig {

    /**
     * Enable extension metrics (true by default)
     */
    @ConfigItem(defaultValue = "true")
    protected boolean enabled;
}
