package com.hififilter.audit.logs.client.runtime.audit.exception;

import io.quarkus.rest.client.reactive.runtime.ResteasyReactiveResponseExceptionMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.client.impl.RestClientRequestContext;

/**
 * Exception mapper. Gotta catch 'em all!
 */
@ApplicationScoped
public class AuditLogClientExceptionMapper implements ResteasyReactiveResponseExceptionMapper<Exception> {

    @Override
    public Exception toThrowable(final Response response, final RestClientRequestContext context) {
        // We just read the entity to trigger the ReaderInterceptor and read/send the audit log
        if (response.bufferEntity()) {
            response.readEntity(String.class);
        }
        return null;
    }
}
