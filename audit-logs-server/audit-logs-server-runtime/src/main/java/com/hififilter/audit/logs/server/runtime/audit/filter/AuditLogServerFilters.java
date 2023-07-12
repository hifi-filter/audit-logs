package com.hififilter.audit.logs.server.runtime.audit.filter;

import com.hififilter.audit.logs.common.runtime.audit.Loggers;
import com.hififilter.audit.logs.server.runtime.audit.service.AuditLogServerService;
import io.smallrye.mutiny.Uni;
import io.vertx.core.http.HttpServerRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ResourceInfo;
import java.util.Optional;
import org.jboss.resteasy.reactive.server.ServerRequestFilter;
import org.jboss.resteasy.reactive.server.ServerResponseFilter;

/**
 * RestEasy filters to manage audit log
 */
@ApplicationScoped
public class AuditLogServerFilters {

    /**
     * Audit log service
     */
    @Inject
    protected AuditLogServerService auditLogService;

    /**
     * Set HTTP request infos
     *
     * @param request Request context
     * @param httpRequest HTTP Request (Vert.X low level object)
     */
    @ServerRequestFilter(preMatching = true, priority = Priorities.AUTHENTICATION - 500)
    public void setRequestInfos(final ContainerRequestContext request, final HttpServerRequest httpRequest) {
        try {
            auditLogService.initRequestInfos(
                request.getMethod(),
                request.getUriInfo().getPath(),
                request.getUriInfo().getQueryParameters(),
                request.getHeaders(),
                httpRequest.remoteAddress()
            );
        } catch (RuntimeException ex) {
            Loggers.AUDIT_LOGS.error(
                "Error when setting server request infos {}",
                () -> request.getUriInfo().getPath(),
                () -> ex
            );
        }
    }

    /**
     * Set HTTP user infos
     *
     * @param request Request context
     */
    @ServerRequestFilter(preMatching = true, priority = Priorities.AUTHENTICATION + 500)
    public void setUserInfos(final ContainerRequestContext request) {
        try {
            auditLogService.initUserInfos(request.getSecurityContext().getUserPrincipal());
        } catch (RuntimeException ex) {
            Loggers.AUDIT_LOGS.error(
                "Error when setting server user infos {}",
                () -> request.getSecurityContext().getUserPrincipal(),
                () -> ex
            );
        }
    }

    /**
     * Set resource method infos, HTTP header and body
     *
     * @param resourceInfo ResourceInfo
     */
    @ServerRequestFilter(priority = 0)
    public void setResourceMethodInfos(final ResourceInfo resourceInfo) {
        try {
            auditLogService.initResourceMethodInfos(
                Optional.ofNullable(resourceInfo).map(ResourceInfo::getResourceMethod).orElse(null)
            );
        } catch (RuntimeException ex) {
            Loggers.AUDIT_LOGS.error("Error when setting server resource method infos", ex);
        }
    }

    /**
     * Send audit log
     *
     * @param response Response context
     * @return void
     */
    @ServerResponseFilter
    public Uni<Void> send(final ContainerResponseContext response) {
        try {
            auditLogService.setResponse(response);
            auditLogService.setResponseBody(response.getEntity());
            return auditLogService.send();
        } catch (RuntimeException ex) {
            Loggers.AUDIT_LOGS.error("Error when sending server response audit log", ex);
            return Uni.createFrom().voidItem();
        }
    }
}
