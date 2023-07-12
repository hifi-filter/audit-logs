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
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import java.util.Map;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.reactive.RestQuery;

/**
 * My rest client
 */
@Path("/test")
@RegisterRestClient(configKey = "my-rest-client")
public interface MyRestClient {

    /**
     * Audit log test endpoint
     *
     * @return A string
     */
    @GET
    @Path("/enabled")
    @Produces(MediaType.TEXT_PLAIN)
    String auditEnabled();

    /**
     * Audit log disabled test endpoint
     *
     * @return A string
     */
    @GET
    @Path("/disabled")
    @Produces(MediaType.TEXT_PLAIN)
    @AuditLogDisabled
    String auditDisabled();

    /**
     * Audit log test endpoint
     *
     * @return A string
     */
    @GET
    @Path("/confused")
    @Produces(MediaType.TEXT_PLAIN)
    @AuditLogEnabled
    @AuditLogDisabled
    String auditConfused();

    /**
     * Audit log query params test
     *
     * @param param Query param
     * @return A string
     */
    @GET
    @Path("/query-params")
    @Produces(MediaType.TEXT_PLAIN)
    String auditQueryParams(@RestQuery("p") final String param);

    /**
     * Audit log action test
     *
     * @return A string
     */
    @GET
    @Path("/action")
    @Produces(MediaType.TEXT_PLAIN)
    @AuditLogOptions(action = "ACTION")
    String auditAction();

    /**
     * Audit log test uni endpoint
     *
     * @return A string
     */
    @GET
    @Path("/uni")
    @Produces(MediaType.TEXT_PLAIN)
    Uni<String> auditUni();

    /**
     * Audit log test multi endpoint
     *
     * @return A string
     */
    @GET
    @Path("/multi")
    @Produces(MediaType.TEXT_PLAIN)
    Multi<String> auditMulti();

    /**
     * Audit failure
     *
     * @return Nothing, it will always throw
     * @throws UnsupportedOperationException Exception
     */
    @GET
    @Path("/failure")
    @Produces(MediaType.TEXT_PLAIN)
    String auditFailure();

    /**
     * Audit failure on uni
     *
     * @return A failure
     */
    @GET
    @Path("/uni/failure")
    @Produces(MediaType.TEXT_PLAIN)
    Uni<String> auditUniFailure();

    /**
     * Audit log test no response body endpoint
     *
     * @param myVar A parameter
     * @return A string
     */
    @POST
    @Path("/no-request-body")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    @AuditLogOptions(
        request = @AuditLogHttpOptions(
            logEntity = false
        )
    )
    String auditNoRequestBody(final String myVar);

    /**
     * Audit log test no response body endpoint
     *
     * @param myVar A parameter
     * @return A string
     */
    @POST
    @Path("/no-response-body")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    @AuditLogOptions(
        response = @AuditLogHttpOptions(
            logEntity = false
        )
    )
    String auditNoResponseBody(final String myVar);

    /**
     * Audit log custom fields test
     *
     * @param myVar A parameter
     * @return A string
     */
    @POST
    @Path("/custom-fields")
    @Produces(MediaType.TEXT_PLAIN)
    @AuditLogOptions(
        request = @AuditLogHttpOptions(customFieldGeneratorClass = MyCustomFields.class)
    )
    String auditCustomFields(final String myVar);

    /**
     * Audit log custom fields inner class test
     *
     * @param myVar A parameter
     * @return A string
     */
    @POST
    @Path("/custom-fields-inner")
    @Produces(MediaType.TEXT_PLAIN)
    @AuditLogOptions(
        request = @AuditLogHttpOptions(customFieldGeneratorClass = MyInnerCustomFields.class)
    )
    String auditCustomFieldsInner(final String myVar);

    /**
     * Audit log test removed header
     *
     * @param token A token
     * @return A string
     */
    @GET
    @Path("/removed-header")
    @Produces(MediaType.TEXT_PLAIN)
    String auditRemovedHeader(@HeaderParam(HttpHeaders.AUTHORIZATION) final String token);

    /**
     * Audit log test removed header lowercase
     *
     * @param token A token
     * @return A string
     */
    @GET
    @Path("/removed-header-lowercase")
    @Produces(MediaType.TEXT_PLAIN)
    String auditRemovedHeaderLowerCase(@HeaderParam("authorization") final String token);

    /**
     * Audit log test removed header uppercase
     *
     * @param token A token
     * @return A string
     */
    @GET
    @Path("/removed-header-uppercase")
    @Produces(MediaType.TEXT_PLAIN)
    String auditRemovedHeaderUpperCase(@HeaderParam("AUTHORIZATION") final String token);

    /**
     * Audit log test void return
     */
    @POST
    @Path("/void")
    void auditVoid();

    /**
     * Audit log test void failure return
     */
    @POST
    @Path("/void/failure")
    void auditVoidFailure();

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
