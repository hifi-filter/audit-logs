package com.hififilter.audit.logs.client.runtime.audit.utils;

import java.lang.reflect.Method;
import java.util.Optional;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.reactive.client.impl.ClientRequestContextImpl;
import org.jboss.resteasy.reactive.client.spi.ResteasyReactiveClientRequestContext;

/**
 * Request context utils
 */
public final class ContextUtils {

    /**
     * Audit log uuid property name
     */
    public static final String AUDIT_LOG_UUID_PROPERTY = "auditLogUUID";

    /**
     * Invoked method property
     */
    private static final String INVOKED_METHOD_PROPERTY = "org.eclipse.microprofile.rest.client.invokedMethod";

    /**
     * Constructor
     */
    private ContextUtils() {
    }

    /**
     * Get invoked method
     *
     * @param request Context
     * @return Invoked method
     */
    public static Optional<Method> getInvokedMethod(final ResteasyReactiveClientRequestContext request) {
        if (request instanceof ClientRequestContextImpl context) {
            if (context.getRestClientRequestContext().getProperties()
                .getOrDefault(INVOKED_METHOD_PROPERTY, null) instanceof Method method) {
                return Optional.of(method);
            }
        }
        return Optional.empty();
    }

    /**
     * Get client name
     *
     * @param invokedMethod Invoked method
     * @return Client name
     */
    public static Optional<String> getClientName(final Optional<Method> invokedMethod) {
        return invokedMethod
            .map(Method::getDeclaringClass)
            .map(clazz -> clazz.getAnnotation(RegisterRestClient.class))
            .map(RegisterRestClient::configKey);
    }
}
