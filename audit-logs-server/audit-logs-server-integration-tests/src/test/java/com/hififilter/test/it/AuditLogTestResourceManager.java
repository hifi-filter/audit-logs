package com.hififilter.test.it;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import java.util.Collections;
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

    @Override
    public Map<String, String> start() {
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
        return Collections.singletonMap("quarkus.hifi-filter.audit-logs.endpoint", "http://localhost:" + SERVER_PORT);
    }

    @Override
    public void stop() {
        server.close();
    }
}
