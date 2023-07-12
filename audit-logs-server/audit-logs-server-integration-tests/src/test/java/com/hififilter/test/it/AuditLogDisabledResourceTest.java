package com.hififilter.test.it;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.concurrent.TimeUnit;
import org.hamcrest.Matchers;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * Tests of {@link AuditLogDisabledResource}
 */
@QuarkusTest
@TestHTTPEndpoint(AuditLogDisabledResource.class)
@QuarkusTestResource(AuditLogTestResourceManager.class)
public class AuditLogDisabledResourceTest {

    /**
     * Timeout used to retrieve logs
     */
    private static final int WAIT_FOR_LOGS_TIMEOUT = 1000;

    @Test
    public void testAuditDisabled() throws InterruptedException {
        RestAssured.given()
            .when()
            .get()
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body(Matchers.is("audit disabled"));

        var log = AuditLogTestResourceManager.LOGS.poll(WAIT_FOR_LOGS_TIMEOUT, TimeUnit.MILLISECONDS);
        assertNull(log);
    }

    @Test
    public void testAuditEnabled() throws InterruptedException {
        RestAssured.given()
            .when()
            .get("/enabled")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body(Matchers.is("audit enabled"));

        var log = AuditLogTestResourceManager.LOGS.poll(WAIT_FOR_LOGS_TIMEOUT, TimeUnit.MILLISECONDS);
        assertNotNull(log);
        assertFalse(log.containsKey("action"));

        // user
        var user = log.getJsonObject("user");
        assertNotNull(user);
        assertFalse(user.containsKey("id"));
        assertTrue(user.containsKey("remoteAddress"));

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
        assertEquals("audit enabled", response.getString("body"));
        assertFalse(response.containsKey("customFields"));
    }

    @Test
    public void testAuditConfused() throws InterruptedException {
        RestAssured.given()
            .when()
            .get("/confused")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body(Matchers.is("audit confused"));

        var log = AuditLogTestResourceManager.LOGS.poll(WAIT_FOR_LOGS_TIMEOUT, TimeUnit.MILLISECONDS);
        assertNull(log);
    }
}
