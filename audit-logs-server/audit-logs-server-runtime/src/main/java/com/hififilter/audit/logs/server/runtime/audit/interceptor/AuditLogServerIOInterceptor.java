package com.hififilter.audit.logs.server.runtime.audit.interceptor;

import com.hififilter.audit.logs.server.runtime.audit.service.AuditLogServerService;
import jakarta.inject.Inject;
import jakarta.ws.rs.ConstrainedTo;
import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.ext.ReaderInterceptor;
import jakarta.ws.rs.ext.ReaderInterceptorContext;
import java.io.IOException;

/**
 * Request interceptor
 */
@Provider
@ConstrainedTo(RuntimeType.SERVER)
public class AuditLogServerIOInterceptor implements ReaderInterceptor {

    /**
     * Audit log service
     */
    @Inject
    protected AuditLogServerService auditLogServerService;

    @Override
    public Object aroundReadFrom(final ReaderInterceptorContext context) throws IOException {
        Object entity = context.proceed();
        auditLogServerService.setRequestBody(entity);
        return entity;
    }
}
