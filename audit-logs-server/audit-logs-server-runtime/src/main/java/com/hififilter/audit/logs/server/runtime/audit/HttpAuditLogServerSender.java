package com.hififilter.audit.logs.server.runtime.audit;

import com.hififilter.audit.logs.common.runtime.audit.bean.AuditLog;
import com.hififilter.audit.logs.server.runtime.AuditLogsServerConfig;
import com.hififilter.audit.logs.server.runtime.audit.annotations.ServerSender;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.Uni;
import io.vertx.core.http.HttpClientOptions;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ContextResolver;
import java.net.URI;
import org.apache.logging.log4j.Level;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/**
 * HTTP Audit log sender. Send audit log to an HTTP endpoint on JSON format with Resteasy Client
 */
@ApplicationScoped
@ServerSender
public class HttpAuditLogServerSender implements AuditLogServerSender {

    /**
     * HTTP Timeout in seconds.<br />
     *
     * <p>Keep it low is a good idea to protect the latency of applications.</p>
     */
    private static final int HTTP_TIMEOUT_IN_MS = 200;

    /**
     * Audit logs extension runtime config
     */
    @Inject
    protected AuditLogsServerConfig auditLogsConfig;

    /**
     * Rest client
     */
    private RestClient restClient;

    @Override
    public Uni<Void> send(final AuditLog auditLog) {
        if (restClient != null) {
            return restClient.sendAuditLog(auditLog)
                .invoke(response -> {
                    var success = response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL;
                    Loggers.AUDIT_LOGS_SERVER.log(success ? Level.TRACE : Level.ERROR,
                        "Audit log HTTP response: {}", response.getStatus());
                })
                .replaceWithVoid();
        } else {
            return Uni.createFrom().voidItem();
        }
    }

    /**
     * Method executed on start. Subscribe to processor to send audit log
     *
     * @param event Startup event
     */
    protected void onStart(@Observes final StartupEvent event) {
        if (auditLogsConfig.server().enabled()) {
            if (auditLogsConfig.server().endpoint().isPresent()) {
                restClient = RestClientBuilder.newBuilder()
                    .baseUri(URI.create(auditLogsConfig.server().endpoint().get()))
                    .register((ContextResolver<HttpClientOptions>) type -> {
                        var options = new HttpClientOptions();
                        options.setConnectTimeout(HTTP_TIMEOUT_IN_MS);
                        return options;
                    })
                    .build(RestClient.class);
            } else {
                Loggers.AUDIT_LOGS_SERVER.warn("Audit log is enabled but endpoint is missing");
            }
        }
    }

    /**
     * Reactive rest client
     */
    @Path("")
    @RegisterRestClient
    protected static interface RestClient {

        /**
         * Send audit log
         *
         * @param log Audit log
         * @return Response
         */
        @POST
        @Consumes(MediaType.APPLICATION_JSON)
        Uni<Response> sendAuditLog(final AuditLog log);
    }

}
