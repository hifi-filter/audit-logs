package com.hififilter.audit.logs.client.deployment.jar;

import com.hififilter.audit.logs.client.runtime.AuditLogsClientConfig;
import io.quarkus.test.QuarkusUnitTest;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.inject.Inject;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

/**
 * Audit logs extension test
 */
public class AuditLogsDefaultConfigJarTest {

    /**
     * Register extension
     */
    @RegisterExtension
    protected static final QuarkusUnitTest UNIT_TEST = new QuarkusUnitTest()
        .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class));

    /**
     * Audit logs extension runtime config
     */
    @Inject
    protected AuditLogsClientConfig auditLogsConfig;

    @Test
    @ActivateRequestContext
    public void testValidConfig() {
        assertTrue(auditLogsConfig.clientsDefault().enabled());
        assertTrue(auditLogsConfig.clientsDefault().endpoint().isEmpty());
    }
}
