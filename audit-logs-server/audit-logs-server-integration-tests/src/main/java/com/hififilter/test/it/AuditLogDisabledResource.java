package com.hififilter.test.it;

import com.hififilter.audit.logs.common.runtime.audit.annotations.AuditLogDisabled;
import com.hififilter.audit.logs.common.runtime.audit.annotations.AuditLogEnabled;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * Audit log test resource
 */
@Path("/disabled")
@AuditLogDisabled
public class AuditLogDisabledResource {

    /**
     * Audit log test endpoint
     *
     * @return A string
     */
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public String audit() {
        return "audit disabled";
    }

    /**
     * Audit log test endpoint
     *
     * @return A string
     */
    @GET
    @Path("/enabled")
    @Produces(MediaType.APPLICATION_JSON)
    @AuditLogEnabled
    public String auditEnabled() {
        return "audit enabled";
    }

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
    public String auditConfused() {
        return "audit confused";
    }
}
