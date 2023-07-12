package com.hififilter.audit.logs.server.deployment.jar;

import com.hififilter.audit.logs.server.runtime.AuditLogsServerConfig;
import io.quarkus.test.QuarkusUnitTest;
import jakarta.inject.Inject;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

/**
 * Audit logs extension invalid config test
 */
public class AuditLogsDefaultConfigJarTest {

    /**
     * Register extension
     */
    @RegisterExtension
    protected static final QuarkusUnitTest UNIT_TEST = new QuarkusUnitTest()
        .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class))
        .assertException(e -> {
            assertSame(IllegalArgumentException.class, e.getClass());
            assertTrue(e.getMessage().contains("quarkus.hifi-filter.audit-logs.endpoint"));
        });

    /**
     * Audit logs extension runtime config
     */
    @Inject
    protected AuditLogsServerConfig auditLogsConfig;

    @Test
    public void testMissingEndpointThrowsIllegalArgumentException() {
        assertTrue(auditLogsConfig.server().enabled());
        // The extension registration fails because the extension is enabled but there's no endpoint defined
        fail("Should not be call because the extension registration fails before !");
    }
}
