package com.hififilter.audit.logs.common.runtime;

import io.quarkus.runtime.annotations.Recorder;
import io.quarkus.runtime.metrics.MetricsFactory;
import java.util.function.Consumer;

/**
 * Audit logs recorder
 */
@Recorder
public class AuditLogsRecorder {

    /**
     * Register extensions metrics
     *
     * @return A MetricsFactory consumer
     */
    public Consumer<MetricsFactory> registerMetrics() {
        return metricsFactory -> {
            metricsFactory.builder(AuditLogsMetrics.SEND_SUCCESS)
                .description("Counter of audit log send success")
                .buildCounter(AuditLogsMetrics.SEND_SUCCESS_COUNTER::longValue);
            metricsFactory.builder(AuditLogsMetrics.SEND_FAILED)
                .description("Counter of audit log send failed")
                .buildCounter(AuditLogsMetrics.SEND_FAILED_COUNTER::longValue);
        };
    }
}
