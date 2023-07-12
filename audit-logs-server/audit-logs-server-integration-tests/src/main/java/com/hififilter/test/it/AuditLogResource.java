package com.hififilter.test.it;

import com.hififilter.audit.logs.common.runtime.audit.AuditLogCustomFieldsGenerator;
import com.hififilter.audit.logs.common.runtime.audit.annotations.AuditLogDisabled;
import com.hififilter.audit.logs.common.runtime.audit.annotations.AuditLogEnabled;
import com.hififilter.audit.logs.common.runtime.audit.annotations.AuditLogHttpOptions;
import com.hififilter.audit.logs.common.runtime.audit.annotations.AuditLogOptions;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import java.util.Map;

/**
 * Audit log test resource
 */
@Path("/test")
public class AuditLogResource {

    /**
     * Audit log test endpoint
     *
     * @return A string
     */
    @GET
    @Path("/enabled")
    @Produces(MediaType.APPLICATION_JSON)
    public String auditEnabled() {
        return "audit enabled";
    }

    /**
     * Audit log disabled test endpoint
     *
     * @return A string
     */
    @GET
    @Path("/disabled")
    @Produces(MediaType.APPLICATION_JSON)
    @AuditLogDisabled
    public String auditDisabled() {
        return "audit disabled";
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

    /**
     * Audit log query params test
     *
     * @param param Query param
     * @return A string
     */
    @GET
    @Path("/query-params")
    @Produces(MediaType.APPLICATION_JSON)
    public String auditQueryParams(@QueryParam("p") final String param) {
        return "audit query params: " + param;
    }

    /**
     * Audit log action test
     *
     * @return A string
     */
    @GET
    @Path("/action")
    @Produces(MediaType.APPLICATION_JSON)
    @AuditLogOptions(action = "ACTION")
    public String auditAction() {
        return "audit action";
    }

    /**
     * Audit log test uni endpoint
     *
     * @return A string
     */
    @GET
    @Path("/uni")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<String> auditUni() {
        return Uni.createFrom().item("audit uni");
    }

    /**
     * Audit log test multi endpoint
     *
     * @return A string
     */
    @GET
    @Path("/multi")
    @Produces(MediaType.APPLICATION_JSON)
    public Multi<String> auditMulti() {
        return Multi.createFrom().item("audit multi");
    }

    /**
     * Audit failure
     *
     * @return Nothing, it will always throw
     * @throws UnsupportedOperationException Exception
     */
    @GET
    @Path("/failure")
    @Produces(MediaType.APPLICATION_JSON)
    public String auditFailure() {
        throw new UnsupportedOperationException("fail");
    }

    /**
     * Audit failure on uni
     *
     * @return A failure
     */
    @GET
    @Path("/uni/failure")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<String> auditUniFailure() {
        return Uni.createFrom().failure(new UnsupportedOperationException("fail"));
    }

    /**
     * Audit log test no response body endpoint
     *
     * @param myVar A parameter
     * @return A string
     */
    @POST
    @Path("/no-request-body")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @AuditLogOptions(
        request = @AuditLogHttpOptions(
            logEntity = false
        )
    )
    public String auditNoRequestBody(final String myVar) {
        return "audit no request body";
    }

    /**
     * Audit log test no response body endpoint
     *
     * @param myVar A parameter
     * @return A string
     */
    @POST
    @Path("/no-response-body")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @AuditLogOptions(
        response = @AuditLogHttpOptions(
            logEntity = false
        )
    )
    public String auditNoResponseBody(final String myVar) {
        return "audit no response body";
    }

    /**
     * Audit log custom fields test
     *
     * @param myVar A parameter
     * @return A string
     */
    @POST
    @Path("/custom-fields")
    @Produces(MediaType.APPLICATION_JSON)
    @AuditLogOptions(
        request = @AuditLogHttpOptions(customFieldGeneratorClass = MyCustomFields.class)
    )
    public String auditCustomFields(final String myVar) {
        return "audit custom fields";
    }

    /**
     * Audit log custom fields inner class test
     *
     * @param myVar A parameter
     * @return A string
     */
    @POST
    @Path("/custom-fields-inner")
    @Produces(MediaType.APPLICATION_JSON)
    @AuditLogOptions(
        request = @AuditLogHttpOptions(customFieldGeneratorClass = MyInnerCustomFields.class)
    )
    public String auditCustomFieldsInner(final String myVar) {
        return "audit custom fields";
    }

    /**
     * Audit log test void endpoint
     */
    @POST
    @Path("/void")
    public void auditVoid() {
    }

    /**
     * Inner class custom field generator
     */
    public static class MyInnerCustomFields implements AuditLogCustomFieldsGenerator {

        @Override
        public Map<String, Object> generate(final Object entity) {
            return Map.of("foo", "bar");
        }
    }
}
