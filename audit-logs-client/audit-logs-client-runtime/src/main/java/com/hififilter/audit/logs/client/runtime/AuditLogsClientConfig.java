package com.hififilter.audit.logs.client.runtime;

import com.hififilter.audit.logs.common.runtime.AuditLogsConfig;
import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import java.util.Map;
import lombok.Getter;

/**
 * Audit logs extension configuration
 */
@ConfigRoot(name = "hifi-filter.audit-logs", phase = ConfigPhase.RUN_TIME)
public class AuditLogsClientConfig {

    /**
     * Default clients config
     */
    @Getter
    @ConfigItem(name = "clients")
    protected AuditLogsConfig clientsDefault;

    /**
     * Client configs (properties will override default config if set)
     */
    @Getter
    @ConfigItem(name = ConfigItem.PARENT)
    protected Map<String, AuditLogsConfig> clients;

    /**
     * Get client config or default config if not set
     *
     * @param clientName Client name
     * @return A Client config
     */
    public AuditLogsConfig getClientOrDefault(final String clientName) {
        if (!clients().containsKey(clientName)) {
            return clientsDefault();
        }
        var clientConfig = clients().get(clientName);
        return new AuditLogsConfig(
            clientConfig.enabled(),
            clientConfig.endpoint().or(() -> clientsDefault().endpoint())
        );
    }
}
