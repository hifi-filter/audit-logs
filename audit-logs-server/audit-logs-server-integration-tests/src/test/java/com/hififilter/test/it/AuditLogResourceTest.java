package com.hififilter.test.it;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.hamcrest.Matchers;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * Audit logs it tests of {@link com.hififilter.test.it.AuditLogResource}
 */
@QuarkusTest
@TestHTTPEndpoint(AuditLogResource.class)
@QuarkusTestResource(AuditLogTestResourceManager.class)
public class AuditLogResourceTest {

    /**
     * Timeout used to retrieve logs
     */
    private static final int WAIT_FOR_LOGS_TIMEOUT = 1000;

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
        assertEquals("audit enabled", response.getString("body"));
        assertFalse(response.containsKey("customFields"));
    }

    @Test
    public void testAuditDisabled() throws InterruptedException {
        RestAssured.given()
            .when()
            .get("/disabled")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body(Matchers.is("audit disabled"));

        var log = AuditLogTestResourceManager.LOGS.poll(WAIT_FOR_LOGS_TIMEOUT, TimeUnit.MILLISECONDS);
        assertNull(log);
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

    @Test
    public void testAuditQueryParams() throws InterruptedException {
        var param = "toto";
        RestAssured.given()
            .when()
            .get("/query-params?p=" + param)
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body(Matchers.is("audit query params: " + param));

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
        assertEquals("audit query params: " + param, response.getString("body"));
        assertFalse(response.containsKey("customFields"));
    }

    @Test
    public void testAuditAction() throws InterruptedException {
        RestAssured.given()
            .when()
            .get("/action")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body(Matchers.is("audit action"));

        var log = AuditLogTestResourceManager.LOGS.poll(WAIT_FOR_LOGS_TIMEOUT, TimeUnit.MILLISECONDS);
        assertNotNull(log);
        assertEquals("ACTION", log.getString("action"));

        // user
        var user = log.getJsonObject("user");
        assertNotNull(user);
        assertFalse(user.containsKey("id"));
        assertTrue(user.containsKey("remoteAddress"));

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
        assertEquals("audit action", response.getString("body"));
        assertFalse(response.containsKey("customFields"));
    }

    @Test
    public void testAuditUni() throws InterruptedException {
        RestAssured.given()
            .when()
            .get("/uni")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body(Matchers.is("audit uni"));

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
        assertEquals("audit uni", response.getString("body"));
        assertFalse(response.containsKey("customFields"));
    }

    @Test
    public void testAuditMulti() throws InterruptedException {
        RestAssured.given()
            .when()
            .get("/multi")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body(Matchers.is("[\"audit multi\"]"));

        var log = AuditLogTestResourceManager.LOGS.poll(WAIT_FOR_LOGS_TIMEOUT, TimeUnit.MILLISECONDS);
        assertNull(log);
    }

    @Test
    public void testAuditNoRequestBody() throws InterruptedException {
        RestAssured.given()
            .when()
            .contentType(ContentType.JSON)
            .body("foo")
            .post("/no-request-body")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body(Matchers.is("audit no request body"));

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
        assertEquals("audit no request body", response.getString("body"));
        assertFalse(response.containsKey("customFields"));
    }

    @Test
    public void testAuditNoResponseBody() throws InterruptedException {
        RestAssured.given()
            .when()
            .contentType(ContentType.JSON)
            .body("foo")
            .post("/no-response-body")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body(Matchers.is("audit no response body"));

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
        RestAssured.given()
            .when()
            .contentType(ContentType.JSON)
            .body("foo")
            .post("/custom-fields")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body(Matchers.is("audit custom fields"));

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
        assertEquals("audit custom fields", response.getString("body"));
        assertFalse(response.containsKey("customFields"));
    }

    @Test
    public void testAuditCustomFieldsInner() throws InterruptedException {
        RestAssured.given()
            .when()
            .contentType(ContentType.JSON)
            .body("foo")
            .post("/custom-fields-inner")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body(Matchers.is("audit custom fields"));

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
        assertEquals("audit custom fields", response.getString("body"));
        assertFalse(response.containsKey("customFields"));
    }

    @Test
    public void testFailure() throws InterruptedException {
        RestAssured.given()
            .when()
            .get("/failure")
            .then()
            .statusCode(500)
            .contentType(ContentType.JSON);

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
        RestAssured.given()
            .when()
            .get("/uni/failure")
            .then()
            .statusCode(500)
            .contentType(ContentType.JSON);

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
        RestAssured.given()
            .when()
            .header(new Header("Authorization", "Bearer foo"))
            .get("/enabled")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body(Matchers.is("audit enabled"));

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
        RestAssured.given()
            .when()
            .header(new Header("authorization", "Bearer foo"))
            .get("/enabled")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body(Matchers.is("audit enabled"));

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
        RestAssured.given()
            .when()
            .header(new Header("AUTHORIZATION", "Bearer foo"))
            .get("/enabled")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body(Matchers.is("audit enabled"));

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
        RestAssured.given()
            .when()
            .post("/void")
            .then()
            .statusCode(204);

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
        assertEquals("/test/void", request.getString("uri"));
        assertEquals("POST", request.getString("method"));
        assertFalse(request.getJsonObject("headers").getMap().isEmpty());
        assertTrue(request.getJsonObject("queryParams").getMap().isEmpty());
        assertFalse(request.containsKey("body"));
        assertFalse(request.containsKey("customFields"));

        // response
        var response = log.getJsonObject("response");
        assertEquals(204, response.getInteger("status"));
        assertTrue(response.getJsonObject("headers").getMap().isEmpty());
        assertNull(response.getString("body"));
        assertFalse(response.containsKey("customFields"));
    }
}
