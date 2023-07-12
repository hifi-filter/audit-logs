package com.hififilter.audit.logs.common.runtime.audit.service;

import com.hififilter.audit.logs.common.runtime.AuditLogsMetrics;
import com.hififilter.audit.logs.common.runtime.audit.AuditLogCustomFieldsGeneratorFactory;
import com.hififilter.audit.logs.common.runtime.audit.AuditLogOptionsService;
import com.hififilter.audit.logs.common.runtime.audit.AuditLogSender;
import com.hififilter.audit.logs.common.runtime.audit.annotations.AuditLogOptions;
import com.hififilter.audit.logs.common.runtime.audit.bean.AuditLog;
import io.smallrye.mutiny.Uni;
import io.vertx.core.net.SocketAddress;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MultivaluedMap;
import java.lang.reflect.Method;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Getter;

/**
 * Audit log management service.
 */
@ApplicationScoped
public class AuditLogService {

    /**
     * HTTP Headers masked to avoid credentials leak
     */
    private static final Set<String> MASKED_HTTP_HEADER = Set.of("authorization");

    /**
     * HTTP headers used as substitution when masking
     */
    private static final List<String> HTTP_HEADER_SUBSTITUTION_FOR_MASKING = List.of("_masked_");

    /**
     * Java function used to normalize HTTP headers to make it easier to compare
     */
    private static final Function<String, String> HTTP_HEADER_KEY_NORMALIZATION_FUNCTION = String::toLowerCase;

    /**
     * Service used to send AuditLog to external system
     */
    @Inject
    protected AuditLogSender senderService;

    /**
     * Service to get audit log options from invoked method
     */
    @Inject
    protected AuditLogOptionsService auditLogOptionsService;

    /**
     * Factory to get AuditLogCustomFieldsGenerator instance
     */
    @Inject
    protected AuditLogCustomFieldsGeneratorFactory auditLogCustomFieldsGeneratorFactory;

    /**
     * Audit options to apply. Init with default options
     */
    @Getter
    protected AuditLogOptions options;

    /**
     * Init audit log with request infos.
     *
     * @param auditLog Audit log
     * @param method HTTP method
     * @param uri HTTP Request URI
     * @param queryParams HTTP query params
     * @param headers HTTP request headers
     * @param socketAddress User remote address
     */
    public void initRequestInfos(
        final AuditLog auditLog,
        final String method,
        final String uri,
        final Map<String, List<String>> queryParams,
        final Map<String, List<String>> headers,
        final SocketAddress socketAddress) {
        auditLog.request()
            .uri(uri)
            .method(method)
            .queryParams(queryParams)
            .headers(headers);
        if (socketAddress != null) {
            auditLog.user().remoteAddress(socketAddress.hostAddress());
        }
    }

    /**
     * Inject information about user from principal
     *
     * @param auditLog Audit log
     * @param principal Principal (Java security object)
     */
    public void initUserInfos(final AuditLog auditLog, final Principal principal) {
        auditLog.user().id(principal == null ? null : principal.getName());
    }

    /**
     * Return the audit log object or null if audit logs are disabled on method
     *
     * @param auditLog Audit log
     * @param invokedMethod Invoked API resource
     * @return Audit log or null
     */
    public AuditLog isDisabledOnInvokedMethod(final AuditLog auditLog, final Method invokedMethod) {
        // If invokedMethod is null, we consider that audit log is disabled on it
        return invokedMethod == null || auditLogOptionsService.isDisabled(invokedMethod)
            ? null
            : auditLog;
    }

    /**
     * Init resource info method, Audit options will be search on this method
     *
     * @param auditLog Audit log
     * @param invokedMethod Invoked API resource
     */
    public void initResourceMethodInfos(final AuditLog auditLog, final Method invokedMethod) {
        options = auditLogOptionsService.getOptions(invokedMethod);
        auditLog
            .action(options.action())
            .invokedMethod(invokedMethod);
    }

    /**
     * Inject input body
     *
     * @param auditLog Audit log
     * @param entity HTTP body
     */
    public void setRequestBody(final AuditLog auditLog, final Object entity) {
        // If options is null. The request has been stopped before matching the resource
        // In this particular cas, we log body to avoid losing information
        if (options == null) {
            auditLog.request().body(entity);
        } else {
            auditLog.request()
                .customFields(auditLogCustomFieldsGeneratorFactory
                    .getInstance(options.request().customFieldGeneratorClass())
                    .generate(entity))
                .body(options.request().logEntity() ? entity : null);
        }
    }

    /**
     * Set response status and headers
     *
     * @param auditLog Audit log
     * @param status HTTP status
     * @param headers HTTP headers
     */
    public void setResponse(final AuditLog auditLog, final int status, final Map<String, List<String>> headers) {
        auditLog.response()
            .status(status)
            .headers(headers);
    }

    /**
     * Inject input body
     *
     * @param auditLog Audit log
     * @param entity HTTP body
     */
    public void setResponseBody(final AuditLog auditLog, final Object entity) {
        // If options is null. The request has been killed before matching the resource
        // In this particular cas, we log body to avoid losing information
        if (options == null) {
            auditLog.response().body(entity);
        } else {
            auditLog.response()
                .customFields(auditLogCustomFieldsGeneratorFactory
                    .getInstance(options.response().customFieldGeneratorClass())
                    .generate(entity))
                .body(options.response().logEntity() ? entity : null);
        }
    }

    /**
     * Inject a custom field. If the custom field is already defined, it will be silently replaced
     *
     * @param auditLog Audit log
     * @param key Key of custom field
     * @param value Value of custom field
     */
    public void putCustomField(final AuditLog auditLog, final String key, final Object value) {
        auditLog.putCustomField(key, value);
    }

    /**
     * Send audit log to http endpoint.
     *
     * @param auditLog Audit log
     * @return A void uni
     */
    public Uni<Void> send(final AuditLog auditLog) {
        auditLog.setEndDatetimeAsNow();
        return senderService.send(auditLog)
            .onItem()
            .invoke(AuditLogsMetrics.SEND_SUCCESS_COUNTER::increment)
            .onFailure()
            .invoke(AuditLogsMetrics.SEND_FAILED_COUNTER::increment)
            .onFailure()
            .recoverWithNull();
    }

    /**
     * Extract HTTP Headers from MultivaluedMap
     *
     * @param map MultivaluedMap
     * @return MultivaluedMap data
     */
    public static Map<String, List<String>> extractHeaders(final MultivaluedMap<String, String> map) {
        return maskHeaders(extractMultivaluedMap(map, HTTP_HEADER_KEY_NORMALIZATION_FUNCTION));
    }

    /**
     * Extract data from MultivaluedMap
     *
     * @param map MultivaluedMap
     * @return MultivaluedMap data
     */
    public static Map<String, List<String>> extractMultivaluedMap(final MultivaluedMap<String, String> map) {
        return extractMultivaluedMap(map, null);
    }

    /**
     * Extract and normalize data from MultivaluedMap
     *
     * @param map MultivaluedMap
     * @param keyTransformation Function used to transform/normalize key
     * @return MultivaluedMap data
     */
    protected static Map<String, List<String>> extractMultivaluedMap(final MultivaluedMap<String, String> map,
        final Function<String, String> keyTransformation) {
        if (map == null) {
            return Map.of();
        }
        return map.entrySet().stream().collect(Collectors.toMap(
            entry -> keyTransformation == null ? entry.getKey() : keyTransformation.apply(entry.getKey()),
            Map.Entry::getValue
        ));
    }

    /**
     * Masked headers
     *
     * @param headers HTTP headers
     * @return The modified header list
     */
    protected static Map<String, List<String>> maskHeaders(final Map<String, List<String>> headers) {
        if (headers == null) {
            return Map.of();
        }
        var maskedHeaders = new HashMap<>(headers);
        MASKED_HTTP_HEADER.stream().map(HTTP_HEADER_KEY_NORMALIZATION_FUNCTION).forEach(header ->
            maskedHeaders.computeIfPresent(header, (key, values) -> HTTP_HEADER_SUBSTITUTION_FOR_MASKING)
        );
        return maskedHeaders;
    }
}
