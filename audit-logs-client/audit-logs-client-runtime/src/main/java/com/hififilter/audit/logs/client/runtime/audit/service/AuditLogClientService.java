package com.hififilter.audit.logs.client.runtime.audit.service;

import com.hififilter.audit.logs.client.runtime.AuditLogsClientConfig;
import com.hififilter.audit.logs.client.runtime.audit.Loggers;
import com.hififilter.audit.logs.common.runtime.audit.bean.AuditLog;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.core.MultivaluedMap;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Audit log server service
 */
@ApplicationScoped
public class AuditLogClientService {

    /**
     * Custom field name for rest client
     */
    public static final String REST_CLIENT_CUSTOM_FIELD = "rest-client";

    /**
     * Service to manage audit logs
     */
    @Inject
    protected AuditLogService auditLogService;

    /**
     * Audit logs extension runtime config
     */
    @Inject
    protected AuditLogsClientConfig auditLogsConfig;

    /**
     * Audit logs by uuid
     */
    protected Map<String, AuditLog> auditLogs = new HashMap<>();

    /**
     * Init audit log with request infos.
     *
     * @param client Client name
     * @param method HTTP method
     * @param uri HTTP Request URI
     * @param queryParams HTTP query params
     * @param headers HTTP request headers
     * @return Audit log id
     */
    public Optional<String> initRequestInfos(
        final String client,
        final String method,
        final String uri,
        final Map<String, List<String>> queryParams,
        final MultivaluedMap<String, String> headers) {
        if (auditLogsConfig.getClientOrDefault(client).enabled()) {
            var auditLog = new AuditLog();
            auditLogs.put(auditLog.uuid(), auditLog);
            var extractedHeaders = AuditLogService.extractHeaders(headers);
            auditLogService.initRequestInfos(auditLog, method, uri, queryParams, extractedHeaders, null);
            putCustomField(auditLog.uuid(), REST_CLIENT_CUSTOM_FIELD, client);
            return Optional.of(auditLog.uuid());
        }
        return Optional.empty();
    }

    /**
     * Init resource info method, Audit options will be search on this method
     *
     * @param uuid UUID of the audit log
     * @param invokedMethod Invoked API resource
     */
    public void initResourceMethodInfos(final String uuid, final Method invokedMethod) {
        auditLogs.put(
            uuid,
            auditLogService.isDisabledOnInvokedMethod(auditLogs.get(uuid), invokedMethod)
        );
        if (isReady(uuid)) {
            auditLogService.initResourceMethodInfos(auditLogs.get(uuid), invokedMethod);
        }
    }

    /**
     * Inject input body
     *
     * @param uuid UUID of the audit log
     * @param entity HTTP body
     */
    public void setRequestBody(final String uuid, final Object entity) {
        if (isReady(uuid)) {
            auditLogService.setRequestBody(auditLogs.get(uuid), entity);
        }
    }

    /**
     * Inject output information from a ContainerResponseContext object
     *
     * @param uuid UUID of the audit log
     * @param responseContext ClientResponseContext object (JAX-RS object)
     */
    public void setResponse(final String uuid, final ClientResponseContext responseContext) {
        if (isReady(uuid)) {
            auditLogService.setResponse(
                auditLogs.get(uuid),
                responseContext.getStatus(),
                AuditLogService.extractHeaders(responseContext.getHeaders())
            );
        }
    }

    /**
     * Inject input body
     *
     * @param uuid UUID of the audit log
     * @param entity HTTP body
     */
    public void setResponseBody(final String uuid, final Object entity) {
        if (isReady(uuid)) {
            auditLogService.setResponseBody(auditLogs.get(uuid), entity);
        }
    }

    /**
     * Inject a custom field. If the custom field is already defined, it will be silently replaced
     *
     * @param uuid UUID of the audit log
     * @param key Key of custom field
     * @param value Value of custom field
     */
    public void putCustomField(final String uuid, final String key, final Object value) {
        if (isReady(uuid)) {
            auditLogService.putCustomField(auditLogs.get(uuid), key, value);
        }
    }

    /**
     * Send audit log to http endpoint.
     *
     * @param uuid UUID of the audit log
     */
    public synchronized void send(final String uuid) {
        if (isReady(uuid)) {
            var auditLog = auditLogs.remove(uuid);
            auditLogService.send(auditLog)
                .subscribe()
                .with(
                    response -> Loggers.AUDIT_LOGS_CLIENT.trace(
                        "Audit log for {} sent",
                        () -> auditLog.customFields().get(REST_CLIENT_CUSTOM_FIELD)
                    ),
                    error -> Loggers.AUDIT_LOGS_CLIENT.error(
                        "Error while sending audit log for {}",
                        () -> auditLog.customFields().get(REST_CLIENT_CUSTOM_FIELD),
                        () -> error
                    )
                );
        }
    }

    /**
     * Return true if audit log is enabled for the current request
     *
     * @param uuid UUID of audit log
     * @return true if audit log is enabled for the current request
     */
    protected boolean isReady(final String uuid) {
        return auditLogs.containsKey(uuid) && auditLogs.get(uuid) != null;
    }
}
