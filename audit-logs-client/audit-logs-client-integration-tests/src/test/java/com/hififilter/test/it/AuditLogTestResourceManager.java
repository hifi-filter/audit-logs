package com.hififilter.test.it;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;

/**
 * Resource to manage containers to start for IT tests
 */
public class AuditLogTestResourceManager implements QuarkusTestResourceLifecycleManager {

    /**
     * Audit logs
     */
    protected static final BlockingQueue<JsonObject> LOGS = new ArrayBlockingQueue<>(10);

    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(AuditLogTestResourceManager.class);

    /**
     * HttpServer port
     */
    private static final int SERVER_PORT = 9885;

    /**
     * Vertx HttpServer
     */
    private HttpServer server;

    /**
     * WireMockServer
     */
    private WireMockServer wireMockServer;

    @Override
    public Map<String, String> start() {
        startVertxServer();
        startWireMockServer();
        return Map.of(
            "quarkus.hifi-filter.audit-logs.clients.endpoint", "http://localhost:" + SERVER_PORT,
            "quarkus.rest-client.\"my-rest-client\".url", wireMockServer.baseUrl(),
            "quarkus.rest-client.\"my-disabled-rest-client\".url", wireMockServer.baseUrl()
        );
    }

    @Override
    public synchronized void stop() {
        if (server != null) {
            server.close();
            server = null;
        }
        if (wireMockServer != null) {
            wireMockServer.stop();
            wireMockServer = null;
        }
    }

    /**
     * Inject WireMock server
     *
     * @param testInjector Test injector
     */
    @Override
    public void inject(final TestInjector testInjector) {
        testInjector.injectIntoFields(
            wireMockServer,
            new TestInjector.AnnotatedAndMatchesType(InjectWireMock.class, WireMockServer.class)
        );
    }

    /**
     * Start Vertx http server
     */
    protected void startVertxServer() {
        logger.info(() -> "Starting Vertx http server");
        var vertx = Vertx.vertx();
        server = vertx.createHttpServer();
        var router = Router.router(vertx);
        server.requestHandler(router);
        router.route().handler(BodyHandler.create());
        router.route(HttpMethod.POST, "/").handler(ctx -> {
            try {
                LOGS.put(ctx.body().asJsonObject());
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            ctx.response().end();
        });
        server.listen(SERVER_PORT);
        logger.info(() -> "Vertx http server started");
    }

    /**
     * Start WireMock server
     */
    protected void startWireMockServer() {
        logger.info(() -> "Starting WireMock server");
        wireMockServer = new WireMockServer();
        wireMockServer.start();
        logger.info(() -> "WireMock server started");
    }
}
