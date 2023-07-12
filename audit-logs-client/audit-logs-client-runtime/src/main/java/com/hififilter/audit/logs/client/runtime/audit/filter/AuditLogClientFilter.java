package com.hififilter.audit.logs.client.runtime.audit.filter;

import com.hififilter.audit.logs.client.runtime.audit.service.AuditLogClientService;
import com.hififilter.audit.logs.client.runtime.audit.utils.ContextUtils;
import com.hififilter.audit.logs.common.runtime.audit.Loggers;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.client.ClientResponseContext;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.jboss.resteasy.reactive.client.spi.ResteasyReactiveClientRequestContext;
import org.jboss.resteasy.reactive.client.spi.ResteasyReactiveClientRequestFilter;
import org.jboss.resteasy.reactive.client.spi.ResteasyReactiveClientResponseFilter;

/**
 * Client request and response filter
 */
@ApplicationScoped
public class AuditLogClientFilter implements ResteasyReactiveClientRequestFilter, ResteasyReactiveClientResponseFilter {

    /**
     * Audit log service
     */
    @Inject
    protected AuditLogClientService auditLogService;

    @Override
    public void filter(final ResteasyReactiveClientRequestContext request) {
        try {
            var invokedMethod = ContextUtils.getInvokedMethod(request);
            var clientName = ContextUtils.getClientName(invokedMethod);

            if (invokedMethod.isEmpty() || clientName.isEmpty()) {
                return;
            }

            var params = URLEncodedUtils.parse(request.getUri(), StandardCharsets.UTF_8).stream()
                .collect(Collectors.groupingBy(
                    NameValuePair::getName,
                    Collectors.mapping(NameValuePair::getValue, Collectors.toList())
                ));

            var uuid = auditLogService.initRequestInfos(
                clientName.get(),
                request.getMethod(),
                request.getUri().getPath(),
                params,
                request.getStringHeaders()
            );
            if (uuid.isPresent()) {
                request.setProperty(ContextUtils.AUDIT_LOG_UUID_PROPERTY, uuid.get());
                auditLogService.initResourceMethodInfos(uuid.get(), invokedMethod.get());
                auditLogService.setRequestBody(uuid.get(), request.getEntity());
            }
        } catch (RuntimeException ex) {
            Loggers.AUDIT_LOGS.error("Error when filtering client request {}", request::getUri, () -> ex);
        }
    }

    @Override
    public void filter(final ResteasyReactiveClientRequestContext request, final ClientResponseContext response) {
        var uuid = (String) request.getProperty(ContextUtils.AUDIT_LOG_UUID_PROPERTY);
        if (uuid != null) {
            try {
                auditLogService.setResponse(uuid, response);
                // If the reponse succeed and has no entity (method returning void), we send the audit log
                // If the response failed, hasEntity() will return true even on a void method so the exception mapper
                // will send the audit log
                // Warning: if the method returns a Multi, hasEntity() will return false
                if (!response.hasEntity()) {
                    auditLogService.send(uuid);
                }
            } catch (RuntimeException ex) {
                Loggers.AUDIT_LOGS.error("Error when filtering client response {}", request::getUri, () -> ex);
            }
        }
    }
}
