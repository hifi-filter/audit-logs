package com.hififilter.audit.logs.client.runtime.audit.interceptor;

import com.hififilter.audit.logs.client.runtime.audit.service.AuditLogClientService;
import com.hififilter.audit.logs.client.runtime.audit.utils.ContextUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.ConstrainedTo;
import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.ext.ReaderInterceptor;
import jakarta.ws.rs.ext.ReaderInterceptorContext;
import java.io.IOException;

/**
 * Resteasy client Response interceptor
 */
@ApplicationScoped
@ConstrainedTo(RuntimeType.CLIENT)
public class AuditLogClientIOInterceptor implements ReaderInterceptor {

    /**
     * Audit log service
     */
    @Inject
    protected AuditLogClientService auditLogClientService;

    @Override
    public Object aroundReadFrom(final ReaderInterceptorContext context)
        throws IOException, WebApplicationException {
        Object entity = context.proceed();
        var uuid = (String) context.getProperty(ContextUtils.AUDIT_LOG_UUID_PROPERTY);
        if (uuid != null) {
            auditLogClientService.setResponseBody(uuid, entity);
            auditLogClientService.send(uuid);
        }
        return entity;
    }
}
