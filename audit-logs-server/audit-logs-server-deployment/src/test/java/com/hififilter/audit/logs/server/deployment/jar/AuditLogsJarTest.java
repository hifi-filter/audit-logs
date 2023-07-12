package com.hififilter.audit.logs.server.deployment.jar;

import com.hififilter.audit.logs.server.runtime.AuditLogsServerConfig;
import io.quarkus.test.QuarkusUnitTest;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.inject.Inject;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

/**
 * Audit logs extension test
 */
public class AuditLogsJarTest {

    /**
     * Register extension
     */
    @RegisterExtension
    protected static final QuarkusUnitTest UNIT_TEST = new QuarkusUnitTest()
        .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
            .addAsResource(new StringAsset("quarkus.hifi-filter.audit-logs.endpoint=http://foo.bar"), "application.properties"));

    /**
     * Audit logs extension runtime config
     */
    @Inject
    protected AuditLogsServerConfig auditLogsConfig;

    @Test
    @ActivateRequestContext
    public void testValidConfig() {
        assertTrue(auditLogsConfig.server().enabled());
        assertTrue(auditLogsConfig.server().endpoint().isPresent());
        assertEquals("http://foo.bar", auditLogsConfig.server().endpoint().get());
    }
}
