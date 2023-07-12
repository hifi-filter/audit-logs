package com.hififilter.audit.logs.common.runtime;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConvertWith;
import io.quarkus.runtime.configuration.TrimmedStringConverter;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Audit logs extension configuration
 */
@ConfigGroup
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogsConfig {

    /**
     * If set to true, the application will handle audit logs
     */
    @Getter
    @ConfigItem(defaultValue = "true")
    protected boolean enabled;

    /**
     * Define audit log HTTP endpoint (log will be pushed on this endpoint with an HTTP POST request)
     */
    @Getter
    @ConfigItem
    @ConvertWith(TrimmedStringConverter.class)
    protected Optional<String> endpoint;
}
