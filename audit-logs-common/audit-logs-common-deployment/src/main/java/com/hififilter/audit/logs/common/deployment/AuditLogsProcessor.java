package com.hififilter.audit.logs.common.deployment;

import com.hififilter.audit.logs.common.runtime.AuditLogsRecorder;
import com.hififilter.audit.logs.common.runtime.audit.annotations.AuditLogDisabled;
import com.hififilter.audit.logs.common.runtime.audit.annotations.AuditLogEnabled;
import com.hififilter.audit.logs.common.runtime.audit.annotations.AuditLogHttpOptions;
import com.hififilter.audit.logs.common.runtime.audit.annotations.AuditLogOptions;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.BuildSteps;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.IndexDependencyBuildItem;
import io.quarkus.deployment.metrics.MetricsCapabilityBuildItem;
import io.quarkus.deployment.metrics.MetricsFactoryConsumerBuildItem;
import java.util.Optional;

/**
 * Audit log processor
 */
@BuildSteps
public class AuditLogsProcessor {

    /**
     * Feature
     */
    private static final String FEATURE = "audit-logs-common";

    /**
     * Create feature
     *
     * @return An instance of FeatureBuildItem
     */
    @BuildStep
    protected FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    /**
     * Add beans available for CDI injection
     *
     * @return An instance of AdditionalBeanBuildItem
     */
    @BuildStep
    protected AdditionalBeanBuildItem additionalBeans() {
        return new AdditionalBeanBuildItem.Builder()
            .setUnremovable()
            .addBeanClass(AuditLogDisabled.class)
            .addBeanClass(AuditLogEnabled.class)
            .addBeanClass(AuditLogOptions.class)
            .addBeanClass(AuditLogHttpOptions.class)
            .build();
    }

    /**
     * Add classes to jandex index
     *
     * @return An object IndexDependencyBuildItem
     */
    @BuildStep
    protected IndexDependencyBuildItem indexExternalDependency() {
        return new IndexDependencyBuildItem("com.hifi-filter", "audit-logs-common");
    }

    /**
     * Setup metrics
     *
     * @param config Build time config
     * @param recorder Audit log recorder
     * @param metrics Metrics capability
     * @param metricsProducer Metrics build item producer
     */
    @BuildStep
    @Record(ExecutionTime.RUNTIME_INIT)
    protected void setupMetrics(final AuditLogsBuildTimeConfig config,
        final AuditLogsRecorder recorder,
        final Optional<MetricsCapabilityBuildItem> metrics,
        final BuildProducer<MetricsFactoryConsumerBuildItem> metricsProducer) {
        if (config.metrics.enabled && metrics.isPresent()) {
            metricsProducer.produce(new MetricsFactoryConsumerBuildItem(recorder.registerMetrics()));
        }
    }
}
