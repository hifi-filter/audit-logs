package com.hififilter.audit.logs.server.runtime.audit.service;

import com.hififilter.audit.logs.common.runtime.audit.bean.AuditLog;
import com.hififilter.audit.logs.common.runtime.audit.service.AuditLogService;
import com.hififilter.audit.logs.server.runtime.AuditLogsServerConfig;
import io.smallrye.mutiny.Uni;
import io.vertx.core.net.SocketAddress;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.core.MultivaluedMap;
import java.lang.reflect.Method;
import java.security.Principal;

/**
 * Audit log server service
 */
@RequestScoped
public class AuditLogServerService {

    /**
     * Service to manage audit logs
     */
    @Inject
    protected AuditLogService auditLogService;

    /**
     * Audit logs extension runtime config
     */
    @Inject
    protected AuditLogsServerConfig auditLogsConfig;

    /**
     * Audit log
     */
    protected AuditLog auditLog;

    /**
     * Init audit log with request infos.
     *
     * @param method HTTP method
     * @param uri HTTP Request URI
     * @param queryParams HTTP query params
     * @param headers HTTP request headers
     * @param socketAddress User remote address
     */
    public void initRequestInfos(final String method, final String uri,
        final MultivaluedMap<String, String> queryParams,
        final MultivaluedMap<String, String> headers,
        final SocketAddress socketAddress) {
        if (auditLogsConfig.server().enabled()) {
            auditLog = new AuditLog();
            auditLogService.initRequestInfos(
                auditLog,
                method,
                uri,
                AuditLogService.extractMultivaluedMap(queryParams),
                AuditLogService.extractHeaders(headers),
                socketAddress
            );
        }
    }

    /**
     * Inject information about user from principal
     *
     * @param principal Principal (Java security object)
     */
    public void initUserInfos(final Principal principal) {
        if (isReady()) {
            auditLog.user().id(principal == null ? null : principal.getName());
        }
    }

    /**
     * Init resource info method, Audit options will be search on this method
     *
     * @param invokedMethod Invoked API resource
     */
    public void initResourceMethodInfos(final Method invokedMethod) {
        auditLog = auditLogService.isDisabledOnInvokedMethod(auditLog, invokedMethod);
        if (isReady()) {
            auditLogService.initResourceMethodInfos(auditLog, invokedMethod);
        }
    }

    /**
     * Inject input body
     *
     * @param entity HTTP body
     */
    public void setRequestBody(final Object entity) {
        if (isReady()) {
            auditLogService.setRequestBody(auditLog, entity);
        }
    }

    /**
     * Inject output information from a ContainerResponseContext object
     *
     * @param responseContext ContainerResponseContext object (JAX-RS object)
     */
    public void setResponse(final ContainerResponseContext responseContext) {
        if (isReady()) {
            auditLogService.setResponse(
                auditLog,
                responseContext.getStatus(),
                AuditLogService.extractHeaders(responseContext.getStringHeaders())
            );
        }
    }

    /**
     * Inject input body
     *
     * @param entity HTTP body
     */
    public void setResponseBody(final Object entity) {
        if (isReady()) {
            auditLogService.setResponseBody(auditLog, entity);
        }
    }

    /**
     * Inject a custom field. If the custom field is already defined, it will be silently replaced
     *
     * @param key Key of custom field
     * @param value Value of custom field
     */
    public void putCustomField(final String key, final Object value) {
        if (isReady()) {
            auditLogService.putCustomField(auditLog, key, value);
        }
    }

    /**
     * Send audit log to http endpoint.
     *
     * @return Optional with AuditLog uuid if sent (audit log enabled), empty optional otherwise
     */
    public Uni<Void> send() {
        return isReady() ? auditLogService.send(auditLog) : Uni.createFrom().voidItem();
    }

    /**
     * Return true if audit log is enabled for the current request
     *
     * @return true if audit log is enabled for the current request
     */
    protected boolean isReady() {
        return auditLog != null;
    }
}
