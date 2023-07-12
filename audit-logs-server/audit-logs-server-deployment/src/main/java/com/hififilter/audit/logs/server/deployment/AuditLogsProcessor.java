package com.hififilter.audit.logs.server.deployment;

import com.hififilter.audit.logs.server.runtime.AuditLogsRecorder;
import com.hififilter.audit.logs.server.runtime.AuditLogsServerConfig;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.BuildSteps;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.IndexDependencyBuildItem;

/**
 * Audit log processor
 */
@BuildSteps
public class AuditLogsProcessor {

    /**
     * Feature
     */
    private static final String FEATURE = "audit-logs-server";

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
     * Add classes to jandex index
     *
     * @return An object IndexDependencyBuildItem
     */
    @BuildStep
    protected IndexDependencyBuildItem indexExternalDependency() {
        return new IndexDependencyBuildItem("com.hifi-filter", "audit-logs-server");
    }

    /**
     * Build
     *
     * @param recorder Audit logs extension recorder
     * @param config Audit logs extension runtime config
     */
    @BuildStep
    @Record(ExecutionTime.RUNTIME_INIT)
    protected void build(final AuditLogsRecorder recorder, final AuditLogsServerConfig config) {
        recorder.initialize(config);
    }
}
