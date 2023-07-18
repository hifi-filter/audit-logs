package com.hififilter.audit.logs.client.runtime.audit;

import com.hififilter.audit.logs.client.runtime.AuditLogsClientConfig;
import com.hififilter.audit.logs.client.runtime.audit.annotations.ClientSender;
import com.hififilter.audit.logs.client.runtime.audit.service.AuditLogClientService;
import com.hififilter.audit.logs.common.runtime.audit.annotations.AuditLogDisabled;
import com.hififilter.audit.logs.common.runtime.audit.bean.AuditLog;
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
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.Level;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/**
 * HTTP Audit log sender. Send audit log to an HTTP endpoint on JSON format with Resteasy Client
 */
@ApplicationScoped
@ClientSender
public class HttpAuditLogClientSender implements AuditLogClientSender {

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
    protected AuditLogsClientConfig auditLogsConfig;

    /**
     * Rest client
     */
    private final Map<String, RestClient> restClients = new HashMap<>();

    @Override
    public Uni<Void> send(final AuditLog auditLog) {
        var restClient = restClients.get(
            (String) auditLog.customFields().get(AuditLogClientService.REST_CLIENT_CUSTOM_FIELD)
        );
        if (restClient != null) {
            return restClient.sendAuditLog(auditLog)
                .invoke(response -> {
                    var success = response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL;
                    Loggers.AUDIT_LOGS_CLIENT.log(success ? Level.TRACE : Level.ERROR,
                        "Audit log HTTP response: {}", response.getStatus());
                })
                .replaceWithVoid();
        }
        return Uni.createFrom().voidItem();
    }

    /**
     * Method executed on start. Subscribe to processor to send audit log
     *
     * @param event Startup event
     */
    protected void onStart(@Observes final StartupEvent event) {
        auditLogsConfig.clients().entrySet()
            .stream()
            .filter(client -> client.getValue().endpoint().isPresent())
            .forEach(client -> {
                var restClient = RestClientBuilder.newBuilder()
                    .baseUri(URI.create(client.getValue().endpoint().get()))
                    .register((ContextResolver<HttpClientOptions>) type -> {
                        var options = new HttpClientOptions();
                        options.setConnectTimeout(HTTP_TIMEOUT_IN_MS);
                        return options;
                    })
                    .build(RestClient.class);
                restClients.put(client.getKey(), restClient);
            });
    }

    /**
     * Reactive rest client
     */
    @Path("")
    @RegisterRestClient
    @AuditLogDisabled
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
