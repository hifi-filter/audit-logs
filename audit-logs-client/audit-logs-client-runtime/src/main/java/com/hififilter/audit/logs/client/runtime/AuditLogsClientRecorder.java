package com.hififilter.audit.logs.client.runtime;

import io.quarkus.restclient.config.RestClientConfig;
import io.quarkus.restclient.config.RestClientsConfig;
import io.quarkus.runtime.annotations.Recorder;
import java.util.Map;

/**
 * Audit logs client recorder
 */
@Recorder
public class AuditLogsClientRecorder {

    /**
     * Initialize audit log handler
     *
     * @param config Audit logs extension runtime config
     * @param restClientsConfig Rest clients config
     * @throws IllegalArgumentException If audit logs are enabled but no endpoint is defined for a client
     * @throws NoSuchFieldException If rest clients configKey field is not found
     * @throws IllegalAccessException If rest clients configKey field is not accessible
     */
    public void initialize(final AuditLogsClientConfig config, final RestClientsConfig restClientsConfig)
        throws NoSuchFieldException, IllegalAccessException {
        augmentConfig(config, restClientsConfig);
        var clientsWithMissingConfig = config.clients().keySet().stream()
            .filter(k -> config.getClientOrDefault(k).enabled())
            .filter(k -> config.getClientOrDefault(k).endpoint().isEmpty())
            .toList();
        if (!clientsWithMissingConfig.isEmpty()) {
            throw new IllegalArgumentException(
                "Audit-logs are enabled but no endpoint is defined for clients: "
                    + String.join(", ", clientsWithMissingConfig)
            );
        }
    }

    /**
     * Augment config with rest clients config
     *
     * @param config Audit logs extension config
     * @param restClientsConfig Rest clients config
     * @throws NoSuchFieldException If configKey field is not found
     * @throws IllegalAccessException If configKey field is not accessible
     */
    private void augmentConfig(final AuditLogsClientConfig config, final RestClientsConfig restClientsConfig)
        throws NoSuchFieldException, IllegalAccessException {
        // We don't have the choice to use reflection here because the configKey field is package private
        // and have no getter
        var configKeyField = restClientsConfig.getClass().getDeclaredField("configKey");
        configKeyField.setAccessible(true);
        @SuppressWarnings("unchecked")
        var configsByName = (Map<String, RestClientConfig>) configKeyField.get(restClientsConfig);
        configsByName.keySet().forEach(configKey ->
            config.clients().put(configKey, config.getClientOrDefault(configKey))
        );
        // It's not me, it's the neighbor
        configKeyField.setAccessible(false);
    }
}
