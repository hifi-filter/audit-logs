package com.hififilter.test.it;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import java.util.concurrent.TimeUnit;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * Audit logs it tests of {@link MyDisabledRestClient}
 */
@QuarkusTest
@QuarkusTestResource(AuditLogTestResourceManager.class)
public class AuditLogDisabledRestClientTest {

    /**
     * Timeout used to retrieve logs
     */
    private static final int WAIT_FOR_LOGS_TIMEOUT = 1000;

    /**
     * Rest client
     */
    @RestClient
    protected MyDisabledRestClient myRestClient;

    /**
     * Wiremock
     */
    @InjectWireMock
    protected WireMockServer wireMock;

    @Test
    public void testAuditDisabled() throws InterruptedException {
        wireMock.stubFor(
            WireMock
                .get(WireMock.urlEqualTo("/disabled/"))
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
    public void testAuditEnabled() throws InterruptedException {
        wireMock.stubFor(
            WireMock
                .get(WireMock.urlEqualTo("/disabled/enabled"))
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
        assertEquals("/disabled/enabled", request.getString("uri"));
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
    public void testAuditConfused() throws InterruptedException {
        wireMock.stubFor(
            WireMock
                .get(WireMock.urlEqualTo("/disabled/confused"))
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
}
