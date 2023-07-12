package com.hififilter.audit.logs.client.deployment.dev;

import com.hififilter.audit.logs.client.runtime.AuditLogsClientConfig;
import io.quarkus.test.QuarkusDevModeTest;
import jakarta.inject.Inject;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

/**
 * Audit logs extension config test
 */
@Disabled("Injection fail in dev mode 🖕")
public class AuditLogsDefaultConfigDevTest {

    /**
     * Register extension
     */
    @RegisterExtension
    protected static final QuarkusDevModeTest DEV_MODE_TEST = new QuarkusDevModeTest()
        .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class))
        .setAllowFailedStart(true);

    /**
     * Audit logs extension runtime config
     */
    @Inject
    protected AuditLogsClientConfig auditLogsConfig;

    @Test
    public void testMissingEndpointThrowsIllegalArgumentException() {
        assertTrue(auditLogsConfig.clientsDefault().enabled());
        assertFalse(auditLogsConfig.clientsDefault().endpoint().isPresent());
    }
}
