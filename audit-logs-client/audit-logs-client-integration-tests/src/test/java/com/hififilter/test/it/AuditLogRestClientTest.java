package com.hififilter.test.it;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import jakarta.ws.rs.WebApplicationException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * Audit logs it tests of {@link com.hififilter.test.it.MyRestClient}
 */
@QuarkusTest
@QuarkusTestResource(AuditLogTestResourceManager.class)
public class AuditLogRestClientTest {

    /**
     * Timeout used to retrieve logs
     */
    private static final int WAIT_FOR_LOGS_TIMEOUT = 1000;

    /**
     * Rest client
     */
    @RestClient
    protected MyRestClient myRestClient;

    /**
     * Wiremock
     */
    @InjectWireMock
    protected WireMockServer wireMock;

    @Test
    public void testAuditEnabled() throws InterruptedException {
        wireMock.stubFor(
            WireMock
                .get(WireMock.urlEqualTo("/test/enabled"))
                .willReturn(WireMock.aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "text/plain")
                    .withBody("Hello world!")
                )
        );

        var result = myRestClient.auditEnabled();
        assertEquals("Hello world!", result);

        var log = AuditLogTestResourceManager.LOGS.poll(WAIT_FOR_LOGS_TIMEOUT, TimeUnit.MILLISECONDS);
        assertNotNull(log);
        assertFalse(log.containsKey("action"));

        // user
        var user = log.getJsonObject("user");
        assertNotNull(user);
        assertFalse(user.containsKey("id"));
        assertFalse(user.containsKey("remoteAddress"));

        // request
        var request = log.getJsonObject("request");
        assertEquals("/test/enabled", request.getString("uri"));
        assertEquals("GET", request.getString("method"));
        assertFalse(request.getJsonObject("headers").getMap().isEmpty());
        assertTrue(request.getJsonObject("queryParams").getMap().isEmpty());
        assertFalse(request.containsKey("body"));
        assertFalse(request.containsKey("customFields"));

        // response
        var response = log.getJsonObject("response");
        assertEquals(200, response.getInteger("status"));
        assertFalse(response.getJsonObject("headers").getMap().isEmpty());
        assertEquals("Hello world!", response.getString("body"));
        assertFalse(response.containsKey("customFields"));
    }

    @Test
    public void testAuditDisabled() throws InterruptedException {
        wireMock.stubFor(
            WireMock
                .get(WireMock.urlEqualTo("/test/disabled"))
                .willReturn(WireMock.aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "text/plain")
                    .withBody("Hello world!")
                )
        );

        var result = myRestClient.auditDisabled();
        assertEquals("Hello world!", result);

        var log = AuditLogTestResourceManager.LOGS.poll(WAIT_FOR_LOGS_TIMEOUT, TimeUnit.MILLISECONDS);
        assertNull(log);
    }

    @Test
    public void testAuditConfused() throws InterruptedException {
        wireMock.stubFor(
            WireMock
                .get(WireMock.urlEqualTo("/test/confused"))
                .willReturn(WireMock.aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "text/plain")
                    .withBody("Hello world!")
                )
        );

        var result = myRestClient.auditConfused();
        assertEquals("Hello world!", result);

        var log = AuditLogTestResourceManager.LOGS.poll(WAIT_FOR_LOGS_TIMEOUT, TimeUnit.MILLISECONDS);
        assertNull(log);
    }

    @Test
    public void testAuditQueryParams() throws InterruptedException {
        wireMock.stubFor(
            WireMock
                .get(WireMock.urlEqualTo("/test/query-params?p=toto"))
                .willReturn(WireMock.aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "text/plain")
                    .withBody("Hello world!")
                )
        );

        var param = "toto";
        var result = myRestClient.auditQueryParams(param);
        assertEquals("Hello world!", result);

        var log = AuditLogTestResourceManager.LOGS.poll(WAIT_FOR_LOGS_TIMEOUT, TimeUnit.MILLISECONDS);
        assertNotNull(log);
        assertFalse(log.containsKey("action"));

        // user
        var user = log.getJsonObject("user");
        assertNotNull(user);
        assertFalse(user.containsKey("id"));
        assertFalse(user.containsKey("remoteAddress"));

        // request
        var request = log.getJsonObject("request");
        assertEquals("/test/query-params", request.getString("uri"));
        assertEquals("GET", request.getString("method"));
        assertFalse(request.getJsonObject("headers").getMap().isEmpty());
        var queryParams = request.getJsonObject("queryParams").getMap();
        assertTrue(queryParams.containsKey("p"));
        @SuppressWarnings("unchecked")
        var params = (List<String>) queryParams.get("p");
        assertTrue(params.contains(param));
        assertFalse(request.containsKey("body"));
        assertFalse(request.containsKey("customFields"));

        // response
        var response = log.getJsonObject("response");
        assertEquals(200, response.getInteger("status"));
        assertFalse(response.getJsonObject("headers").getMap().isEmpty());
        assertEquals("Hello world!", response.getString("body"));
        assertFalse(response.containsKey("customFields"));
    }

    @Test
    public void testAuditAction() throws InterruptedException {
        wireMock.stubFor(
            WireMock
                .get(WireMock.urlEqualTo("/test/action"))
                .willReturn(WireMock.aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "text/plain")
                    .withBody("Hello world!")
                )
        );

        var result = myRestClient.auditAction();
        assertEquals("Hello world!", result);

        var log = AuditLogTestResourceManager.LOGS.poll(WAIT_FOR_LOGS_TIMEOUT, TimeUnit.MILLISECONDS);
        assertNotNull(log);
        assertEquals("ACTION", log.getString("action"));

        // user
        var user = log.getJsonObject("user");
        assertNotNull(user);
        assertFalse(user.containsKey("id"));
        assertFalse(user.containsKey("remoteAddress"));

        // request
        var request = log.getJsonObject("request");
        assertEquals("/test/action", request.getString("uri"));
        assertEquals("GET", request.getString("method"));
        assertFalse(request.getJsonObject("headers").getMap().isEmpty());
        assertTrue(request.getJsonObject("queryParams").getMap().isEmpty());
        assertFalse(request.containsKey("body"));
        assertFalse(request.containsKey("customFields"));

        // response
        var response = log.getJsonObject("response");
        assertEquals(200, response.getInteger("status"));
        assertFalse(response.getJsonObject("headers").getMap().isEmpty());
        assertEquals("Hello world!", response.getString("body"));
        assertFalse(response.containsKey("customFields"));
    }

    @Test
    public void testAuditUni() throws InterruptedException {
        wireMock.stubFor(
            WireMock
                .get(WireMock.urlEqualTo("/test/uni"))
                .willReturn(WireMock.aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "text/plain")
                    .withBody("Hello world!")
                )
        );

        myRestClient.auditUni()
            .subscribe()
            .withSubscriber(UniAssertSubscriber.create())
            .assertSubscribed()
            .awaitItem()
            .assertItem("Hello world!")
            .assertCompleted();

        var log = AuditLogTestResourceManager.LOGS.poll(WAIT_FOR_LOGS_TIMEOUT, TimeUnit.MILLISECONDS);
        assertNotNull(log);
        assertFalse(log.containsKey("action"));

        // user
        var user = log.getJsonObject("user");
        assertNotNull(user);
        assertFalse(user.containsKey("id"));
        assertFalse(user.containsKey("remoteAddress"));

        // request
        var request = log.getJsonObject("request");
        assertEquals("/test/uni", request.getString("uri"));
        assertEquals("GET", request.getString("method"));
        assertFalse(request.getJsonObject("headers").getMap().isEmpty());
        assertTrue(request.getJsonObject("queryParams").getMap().isEmpty());
        assertFalse(request.containsKey("body"));
        assertFalse(request.containsKey("customFields"));

        // response
        var response = log.getJsonObject("response");
        assertEquals(200, response.getInteger("status"));
        assertFalse(response.getJsonObject("headers").getMap().isEmpty());
        assertEquals("Hello world!", response.getString("body"));
        assertFalse(response.containsKey("customFields"));
    }

    @Test
    public void testAuditMulti() throws InterruptedException {
        wireMock.stubFor(
            WireMock
                .get(WireMock.urlEqualTo("/test/multi"))
                .willReturn(WireMock.aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "text/plain")
                    .withBody("Hello world!")
                )
        );

        myRestClient.auditMulti().collect().asList()
            .subscribe()
            .withSubscriber(UniAssertSubscriber.create())
            .assertSubscribed()
            .awaitItem()
            .assertItem(List.of("Hello world!"))
            .assertCompleted();

        var log = AuditLogTestResourceManager.LOGS.poll(WAIT_FOR_LOGS_TIMEOUT, TimeUnit.MILLISECONDS);
        assertNotNull(log);
        assertFalse(log.containsKey("action"));

        // user
        var user = log.getJsonObject("user");
        assertNotNull(user);
        assertFalse(user.containsKey("id"));
        assertFalse(user.containsKey("remoteAddress"));

        // request
        var request = log.getJsonObject("request");
        assertEquals("/test/multi", request.getString("uri"));
        assertEquals("GET", request.getString("method"));
        assertFalse(request.getJsonObject("headers").getMap().isEmpty());
        assertTrue(request.getJsonObject("queryParams").getMap().isEmpty());
        assertFalse(request.containsKey("body"));
        assertFalse(request.containsKey("customFields"));

        // response
        var response = log.getJsonObject("response");
        assertEquals(200, response.getInteger("status"));
        assertFalse(response.getJsonObject("headers").getMap().isEmpty());
        // Multi is not supported, the response consider there's no entity so the audit log is sent directly
        assertNull(response.getString("body"));
        assertFalse(response.containsKey("customFields"));
    }

    @Test
    public void testAuditNoRequestBody() throws InterruptedException {
        wireMock.stubFor(
            WireMock
                .post(WireMock.urlEqualTo("/test/no-request-body"))
                .willReturn(WireMock.aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "text/plain")
                    .withBody("Hello world!")
                )
        );

        var result = myRestClient.auditNoRequestBody("foo");
        assertEquals("Hello world!", result);

        var log = AuditLogTestResourceManager.LOGS.poll(WAIT_FOR_LOGS_TIMEOUT, TimeUnit.MILLISECONDS);
        assertNotNull(log);
        assertFalse(log.containsKey("action"));

        // user
        var user = log.getJsonObject("user");
        assertNotNull(user);
        assertFalse(user.containsKey("id"));
        assertFalse(user.containsKey("remoteAddress"));

        // request
        var request = log.getJsonObject("request");
        assertEquals("/test/no-request-body", request.getString("uri"));
        assertEquals("POST", request.getString("method"));
        assertFalse(request.getJsonObject("headers").getMap().isEmpty());
        assertTrue(request.getJsonObject("queryParams").getMap().isEmpty());
        assertFalse(request.containsKey("body"));
        assertFalse(request.containsKey("customFields"));

        // response
        var response = log.getJsonObject("response");
        assertEquals(200, response.getInteger("status"));
        assertFalse(response.getJsonObject("headers").getMap().isEmpty());
        assertEquals("Hello world!", response.getString("body"));
        assertFalse(response.containsKey("customFields"));
    }

    @Test
    public void testAuditNoResponseBody() throws InterruptedException {
        wireMock.stubFor(
            WireMock
                .post(WireMock.urlEqualTo("/test/no-response-body"))
                .willReturn(WireMock.aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "text/plain")
                    .withBody("Hello world!")
                )
        );

        var result = myRestClient.auditNoResponseBody("foo");
        assertEquals("Hello world!", result);

        var log = AuditLogTestResourceManager.LOGS.poll(WAIT_FOR_LOGS_TIMEOUT, TimeUnit.MILLISECONDS);
        assertNotNull(log);
        assertFalse(log.containsKey("action"));

        // user
        var user = log.getJsonObject("user");
        assertNotNull(user);
        assertFalse(user.containsKey("id"));
        assertFalse(user.containsKey("remoteAddress"));

        // request
        var request = log.getJsonObject("request");
        assertEquals("/test/no-response-body", request.getString("uri"));
        assertEquals("POST", request.getString("method"));
        assertFalse(request.getJsonObject("headers").getMap().isEmpty());
        assertTrue(request.getJsonObject("queryParams").getMap().isEmpty());
        assertEquals("foo", request.getString("body"));
        assertFalse(request.containsKey("customFields"));

        // response
        var response = log.getJsonObject("response");
        assertEquals(200, response.getInteger("status"));
        assertFalse(response.getJsonObject("headers").getMap().isEmpty());
        assertNull(response.getString("body"));
        assertFalse(response.containsKey("customFields"));
    }

    @Test
    public void testAuditCustomFields() throws InterruptedException {
        wireMock.stubFor(
            WireMock
                .post(WireMock.urlEqualTo("/test/custom-fields"))
                .willReturn(WireMock.aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "text/plain")
                    .withBody("Hello world!")
                )
        );

        var result = myRestClient.auditCustomFields("foo");
        assertEquals("Hello world!", result);

        var log = AuditLogTestResourceManager.LOGS.poll(WAIT_FOR_LOGS_TIMEOUT, TimeUnit.MILLISECONDS);
        assertNotNull(log);
        assertFalse(log.containsKey("action"));

        // user
        var user = log.getJsonObject("user");
        assertNotNull(user);
        assertFalse(user.containsKey("id"));
        assertFalse(user.containsKey("remoteAddress"));

        // request
        var request = log.getJsonObject("request");
        assertEquals("/test/custom-fields", request.getString("uri"));
        assertEquals("POST", request.getString("method"));
        assertFalse(request.getJsonObject("headers").getMap().isEmpty());
        assertTrue(request.getJsonObject("queryParams").getMap().isEmpty());
        assertEquals("foo", request.getString("body"));
        assertNotNull(request.getJsonObject("customFields"));
        var customFields = request.getJsonObject("customFields").getMap();
        assertTrue(customFields.containsKey("foo"));
        assertEquals("bar", customFields.get("foo"));

        // response
        var response = log.getJsonObject("response");
        assertEquals(200, response.getInteger("status"));
        assertFalse(response.getJsonObject("headers").getMap().isEmpty());
        assertEquals("Hello world!", response.getString("body"));
        assertFalse(response.containsKey("customFields"));
    }

    @Test
    public void testAuditCustomFieldsInner() throws InterruptedException {
        wireMock.stubFor(
            WireMock
                .post(WireMock.urlEqualTo("/test/custom-fields-inner"))
                .willReturn(WireMock.aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "text/plain")
                    .withBody("Hello world!")
                )
        );

        var result = myRestClient.auditCustomFieldsInner("foo");
        assertEquals("Hello world!", result);

        var log = AuditLogTestResourceManager.LOGS.poll(WAIT_FOR_LOGS_TIMEOUT, TimeUnit.MILLISECONDS);
        assertNotNull(log);
        assertFalse(log.containsKey("action"));

        // user
        var user = log.getJsonObject("user");
        assertNotNull(user);
        assertFalse(user.containsKey("id"));
        assertFalse(user.containsKey("remoteAddress"));

        // request
        var request = log.getJsonObject("request");
        assertEquals("/test/custom-fields-inner", request.getString("uri"));
        assertEquals("POST", request.getString("method"));
        assertFalse(request.getJsonObject("headers").getMap().isEmpty());
        assertTrue(request.getJsonObject("queryParams").getMap().isEmpty());
        assertEquals("foo", request.getString("body"));
        assertNotNull(request.getJsonObject("customFields"));
        var customFields = request.getJsonObject("customFields").getMap();
        assertTrue(customFields.containsKey("foo"));
        assertEquals("bar", customFields.get("foo"));

        // response
        var response = log.getJsonObject("response");
        assertEquals(200, response.getInteger("status"));
        assertFalse(response.getJsonObject("headers").getMap().isEmpty());
        assertEquals("Hello world!", response.getString("body"));
        assertFalse(response.containsKey("customFields"));
    }

    @Test
    public void testFailure() throws InterruptedException {
        wireMock.stubFor(
            WireMock
                .get(WireMock.urlEqualTo("/test/failure"))
                .willReturn(WireMock.aResponse()
                    .withStatus(500)
                    .withHeader("Content-Type", "text/plain")
                    .withBody("fail")
                )
        );
        assertThrows(WebApplicationException.class, () -> myRestClient.auditFailure());

        var log = AuditLogTestResourceManager.LOGS.poll(WAIT_FOR_LOGS_TIMEOUT, TimeUnit.MILLISECONDS);
        assertNotNull(log);
        assertFalse(log.containsKey("action"));

        // user
        assertTrue(log.containsKey("user"));

        // request
        var request = log.getJsonObject("request");
        assertEquals("/test/failure", request.getString("uri"));
        assertEquals("GET", request.getString("method"));

        // response
        var response = log.getJsonObject("response");
        assertEquals(500, response.getInteger("status"));
        assertEquals("fail", response.getString("body"));
    }

    @Test
    public void testUniFailure() throws InterruptedException {
        wireMock.stubFor(
            WireMock
                .get(WireMock.urlEqualTo("/test/uni/failure"))
                .willReturn(WireMock.aResponse()
                    .withStatus(500)
                    .withHeader("Content-Type", "text/plain")
                    .withBody("fail")
                )
        );
        var failure = myRestClient.auditUniFailure()
            .subscribe()
            .withSubscriber(UniAssertSubscriber.create())
            .assertSubscribed()
            .awaitFailure()
            .assertFailed()
            .getFailure();
        assertTrue(failure instanceof WebApplicationException);

        var log = AuditLogTestResourceManager.LOGS.poll(WAIT_FOR_LOGS_TIMEOUT, TimeUnit.MILLISECONDS);
        assertNotNull(log);
        assertFalse(log.containsKey("action"));

        // user
        assertTrue(log.containsKey("user"));

        // request
        var request = log.getJsonObject("request");
        assertEquals("/test/uni/failure", request.getString("uri"));
        assertEquals("GET", request.getString("method"));

        // response
        var response = log.getJsonObject("response");
        assertEquals(500, response.getInteger("status"));
        assertEquals("fail", response.getString("body"));
    }

    @Test
    public void testRemovedHeader() throws InterruptedException {
        wireMock.stubFor(
            WireMock
                .get(WireMock.urlEqualTo("/test/removed-header"))
                .willReturn(WireMock.aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "text/plain")
                    .withBody("Hello world!")
                )
        );

        var result = myRestClient.auditRemovedHeader("Bearer foo");
        assertEquals("Hello world!", result);

        var log = AuditLogTestResourceManager.LOGS.poll(WAIT_FOR_LOGS_TIMEOUT, TimeUnit.MILLISECONDS);
        assertNotNull(log);

        // request
        var request = log.getJsonObject("request");
        assertTrue(request.getJsonObject("headers").containsKey("authorization"));
        assertFalse(request.getJsonObject("headers").getJsonArray("authorization").isEmpty());
        assertEquals("_masked_", request.getJsonObject("headers").getJsonArray("authorization").getValue(0));
    }

    @Test
    public void testRemovedHeaderLowerCase() throws InterruptedException {
        wireMock.stubFor(
            WireMock
                .get(WireMock.urlEqualTo("/test/removed-header-lowercase"))
                .willReturn(WireMock.aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "text/plain")
                    .withBody("Hello world!")
                )
        );

        var result = myRestClient.auditRemovedHeaderLowerCase("Bearer foo");
        assertEquals("Hello world!", result);

        var log = AuditLogTestResourceManager.LOGS.poll(WAIT_FOR_LOGS_TIMEOUT, TimeUnit.MILLISECONDS);
        assertNotNull(log);

        // request
        var request = log.getJsonObject("request");
        assertTrue(request.getJsonObject("headers").containsKey("authorization"));
        assertFalse(request.getJsonObject("headers").getJsonArray("authorization").isEmpty());
        assertEquals("_masked_", request.getJsonObject("headers").getJsonArray("authorization").getValue(0));
    }

    @Test
    public void testRemovedHeaderUpperCase() throws InterruptedException {
        wireMock.stubFor(
            WireMock
                .get(WireMock.urlEqualTo("/test/removed-header-uppercase"))
                .willReturn(WireMock.aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "text/plain")
                    .withBody("Hello world!")
                )
        );

        var result = myRestClient.auditRemovedHeaderUpperCase("Bearer foo");
        assertEquals("Hello world!", result);

        var log = AuditLogTestResourceManager.LOGS.poll(WAIT_FOR_LOGS_TIMEOUT, TimeUnit.MILLISECONDS);
        assertNotNull(log);

        // request
        var request = log.getJsonObject("request");
        assertTrue(request.getJsonObject("headers").containsKey("authorization"));
        assertFalse(request.getJsonObject("headers").getJsonArray("authorization").isEmpty());
        assertEquals("_masked_", request.getJsonObject("headers").getJsonArray("authorization").getValue(0));
    }

    @Test
    public void testAuditVoid() throws InterruptedException {
        wireMock.stubFor(
            WireMock
                .post(WireMock.urlEqualTo("/test/void"))
                .willReturn(WireMock.created())
        );

        myRestClient.auditVoid();
        var log = AuditLogTestResourceManager.LOGS.poll(WAIT_FOR_LOGS_TIMEOUT, TimeUnit.MILLISECONDS);
        assertNotNull(log);
        assertFalse(log.containsKey("action"));

        // user
        var user = log.getJsonObject("user");
        assertNotNull(user);
        assertFalse(user.containsKey("id"));
        assertFalse(user.containsKey("remoteAddress"));

        // request
        var request = log.getJsonObject("request");
        assertEquals("/test/void", request.getString("uri"));
        assertEquals("POST", request.getString("method"));
        assertFalse(request.getJsonObject("headers").getMap().isEmpty());
        assertTrue(request.getJsonObject("queryParams").getMap().isEmpty());
        assertFalse(request.containsKey("body"));
        assertFalse(request.containsKey("customFields"));

        // response
        var response = log.getJsonObject("response");
        assertEquals(201, response.getInteger("status"));
        assertFalse(response.getJsonObject("headers").getMap().isEmpty());
        assertNull(response.getString("body"));
        assertFalse(response.containsKey("customFields"));
    }

    @Test
    public void testAuditVoidFailure() throws InterruptedException {
        wireMock.stubFor(
            WireMock
                .post(WireMock.urlEqualTo("/test/void/failure"))
                .willReturn(WireMock.aResponse()
                    .withStatus(500)
                    .withHeader("Content-Type", "text/plain")
                    .withBody("fail")
                )
        );

        assertThrows(WebApplicationException.class, () -> myRestClient.auditVoidFailure());
        var log = AuditLogTestResourceManager.LOGS.poll(WAIT_FOR_LOGS_TIMEOUT, TimeUnit.MILLISECONDS);
        assertNotNull(log);
        assertFalse(log.containsKey("action"));

        // user
        var user = log.getJsonObject("user");
        assertNotNull(user);
        assertFalse(user.containsKey("id"));
        assertFalse(user.containsKey("remoteAddress"));

        // request
        var request = log.getJsonObject("request");
        assertEquals("/test/void/failure", request.getString("uri"));
        assertEquals("POST", request.getString("method"));
        assertFalse(request.getJsonObject("headers").getMap().isEmpty());
        assertTrue(request.getJsonObject("queryParams").getMap().isEmpty());
        assertFalse(request.containsKey("body"));
        assertFalse(request.containsKey("customFields"));

        // response
        var response = log.getJsonObject("response");
        assertEquals(500, response.getInteger("status"));
        assertFalse(response.getJsonObject("headers").getMap().isEmpty());
        assertEquals("fail", response.getString("body"));
        assertFalse(response.containsKey("customFields"));
    }
}
