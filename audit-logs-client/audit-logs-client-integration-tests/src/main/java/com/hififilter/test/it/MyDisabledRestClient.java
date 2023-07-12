package com.hififilter.test.it;

import com.hififilter.audit.logs.common.runtime.audit.annotations.AuditLogDisabled;
import com.hififilter.audit.logs.common.runtime.audit.annotations.AuditLogEnabled;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/**
 * My rest client
 */
@Path("/disabled")
@RegisterRestClient(configKey = "my-disabled-rest-client")
@AuditLogDisabled
public interface MyDisabledRestClient {

    /**
     * Audit log test endpoint
     *
     * @return A string
     */
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    String auditDisabled();

    /**
     * Audit log test endpoint
     *
     * @return A string
     */
    @GET
    @Path("/enabled")
    @Produces(MediaType.APPLICATION_JSON)
    @AuditLogEnabled
    String auditEnabled();

    /**
     * Audit log test endpoint
     *
     * @return A string
     */
    @GET
    @Path("/confused")
    @Produces(MediaType.APPLICATION_JSON)
    @AuditLogEnabled
    @AuditLogDisabled
    String auditConfused();
}
