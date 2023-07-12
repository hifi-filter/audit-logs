package com.hififilter.audit.logs.client.deployment;

import com.hififilter.audit.logs.client.runtime.AuditLogsClientConfig;
import com.hififilter.audit.logs.client.runtime.AuditLogsClientRecorder;
import com.hififilter.audit.logs.client.runtime.audit.exception.AuditLogClientExceptionMapper;
import com.hififilter.audit.logs.client.runtime.audit.filter.AuditLogClientFilter;
import com.hififilter.audit.logs.client.runtime.audit.interceptor.AuditLogClientIOInterceptor;
import com.hififilter.audit.logs.common.runtime.audit.annotations.AuditLogDisabled;
import com.hififilter.audit.logs.common.runtime.audit.annotations.AuditLogEnabled;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.BuildSteps;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.IndexDependencyBuildItem;
import io.quarkus.rest.client.reactive.deployment.DotNames;
import io.quarkus.rest.client.reactive.deployment.RegisterProviderAnnotationInstanceBuildItem;
import io.quarkus.restclient.config.RestClientsConfig;
import java.util.List;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationValue;
import org.jboss.jandex.DotName;
import org.jboss.jandex.Type;

/**
 * Audit log processor
 */
@BuildSteps
public class AuditLogsProcessor {

    /**
     * Feature
     */
    private static final String FEATURE = "audit-logs-client";

    /**
     * Register rest client annotation
     */
    private static final DotName REGISTER_REST_CLIENT = DotName.createSimple(RegisterRestClient.class);

    /**
     * Audit log enabled annotation
     */
    private static final DotName AUDIT_LOG_ENABLED = DotName.createSimple(AuditLogEnabled.class);

    /**
     * Audit log disabled annotation
     */
    private static final DotName AUDIT_LOG_DISABLED = DotName.createSimple(AuditLogDisabled.class);

    /**
     * Provider classes to add to rest clients
     */
    private static final List<Class<?>> PROVIDER_CLASSES = List.of(
        AuditLogClientFilter.class,
        AuditLogClientIOInterceptor.class,
        AuditLogClientExceptionMapper.class
    );

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
            .addBeanClass(AuditLogClientFilter.class)
            .addBeanClass(AuditLogClientIOInterceptor.class)
            .addBeanClass(AuditLogClientExceptionMapper.class)
            .build();
    }

    /**
     * Add classes to jandex index
     *
     * @return An object IndexDependencyBuildItem
     */
    @BuildStep
    protected IndexDependencyBuildItem indexExternalDependency() {
        return new IndexDependencyBuildItem("com.hifi-filter", "audit-logs-client");
    }

    /**
     * Build
     *
     * @param recorder Audit logs extension recorder
     * @param config Audit logs extension runtime config
     * @param restClientsConfig Rest clients config
     * @throws NoSuchFieldException If rest clients configKey field is not found
     * @throws IllegalAccessException If rest clients configKey field is not accessible
     */
    @BuildStep
    @Record(ExecutionTime.RUNTIME_INIT)
    protected void build(final AuditLogsClientRecorder recorder, final AuditLogsClientConfig config,
        final RestClientsConfig restClientsConfig) throws NoSuchFieldException, IllegalAccessException {
        recorder.initialize(config, restClientsConfig);
    }

    /**
     * Register provider on rest clients. Who needs documentation when source code exists (check links).
     *
     * @param indexBuildItem Index build item
     * @param producer Build producer
     * @link <a href="https://quarkus.io/guides/all-builditems#rest-client-reactive">All Build Items</a>
     * @link <a href="https://github.com/quarkusio/quarkus/blob/2.16.4.Final/extensions/oidc-token-propagation-reactive/deployment/src/main/java/io/quarkus/oidc/token/propagation/reactive/OidcTokenPropagationReactiveBuildStep.java">OidcTokenPropagationReactiveBuildStep.java</a>
     */
    @BuildStep
    protected void registerProviders(
        final CombinedIndexBuildItem indexBuildItem,
        final BuildProducer<RegisterProviderAnnotationInstanceBuildItem> producer) {
        indexBuildItem.getIndex()
            .getAnnotations(REGISTER_REST_CLIENT)
            .stream()
            .filter(this::shouldRegisterProviders)
            .forEach(instance -> {
                String targetClass = instance.target().asClass().name().toString();
                PROVIDER_CLASSES.forEach(clazz ->
                    producer.produce(
                        new RegisterProviderAnnotationInstanceBuildItem(
                            targetClass,
                            AnnotationInstance.create(
                                DotNames.REGISTER_PROVIDER,
                                instance.target(),
                                List.of(AnnotationValue.createClassValue(
                                    "value",
                                    Type.create(DotName.createSimple(clazz), Type.Kind.CLASS)
                                ))
                            )
                        )
                    )
                );
            });
    }

    /**
     * Condition to register providers
     *
     * @param instance Annotation instance
     * @return True if providers should be registered else false
     */
    protected boolean shouldRegisterProviders(final AnnotationInstance instance) {
        return
            // If the class isn't annotated with @AuditLogDisabled
            instance.target().asClass().declaredAnnotations().stream()
                .noneMatch(annotationInstance -> annotationInstance.name().equals(AUDIT_LOG_DISABLED))
            // Or the class contains an @AuditLogEnabled annotation
            || instance.target().asClass().annotations().stream()
            .anyMatch(annotationInstance -> annotationInstance.name().equals(AUDIT_LOG_ENABLED));
    }
}
