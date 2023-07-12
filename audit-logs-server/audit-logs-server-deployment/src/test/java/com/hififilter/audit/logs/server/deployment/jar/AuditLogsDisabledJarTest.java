package com.hififilter.audit.logs.server.deployment.jar;

import com.hififilter.audit.logs.server.runtime.AuditLogsServerConfig;
import io.quarkus.test.QuarkusUnitTest;
import jakarta.inject.Inject;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

/**
 * Audit logs extension disabled test
 */
public class AuditLogsDisabledJarTest {

    /**
     * Register extension
     */
    @RegisterExtension
    protected static final QuarkusUnitTest UNIT_TEST = new QuarkusUnitTest()
        .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
            .addAsResource(new StringAsset("quarkus.hifi-filter.audit-logs.enabled=false"), "application.properties"));

    /**
     * Audit logs extension runtime config
     */
    @Inject
    protected AuditLogsServerConfig auditLogsConfig;

    @Test
    public void testConfig() {
        assertFalse(auditLogsConfig.server().enabled());
    }
}
