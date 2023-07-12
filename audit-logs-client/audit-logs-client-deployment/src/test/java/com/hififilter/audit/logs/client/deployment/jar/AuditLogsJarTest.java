package com.hififilter.audit.logs.client.deployment.jar;

import com.hififilter.audit.logs.client.runtime.AuditLogsClientConfig;
import io.quarkus.test.QuarkusUnitTest;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.inject.Inject;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
        .setArchiveProducer(() ->
            ShrinkWrap.create(JavaArchive.class)
                .addAsResource(
                    new StringAsset("""
                    quarkus.hifi-filter.audit-logs.toto.endpoint=http://foo.bar
                    quarkus.hifi-filter.audit-logs.tata.enabled=false
                    """),
                    "application.properties"
                )
        );

    /**
     * Audit logs extension runtime config
     */
    @Inject
    protected AuditLogsClientConfig auditLogsConfig;

    @Test
    @ActivateRequestContext
    public void testValidConfig() {
        assertTrue(auditLogsConfig.clients().containsKey("toto"));
        assertTrue(auditLogsConfig.clients().get("toto").enabled());
        assertTrue(auditLogsConfig.clients().get("toto").endpoint().isPresent());
        assertEquals("http://foo.bar", auditLogsConfig.clients().get("toto").endpoint().get());
    }

    @Test
    @ActivateRequestContext
    public void testDisabledClientConfig() {
        assertTrue(auditLogsConfig.clients().containsKey("tata"));
        assertFalse(auditLogsConfig.clients().get("tata").enabled());
        assertTrue(auditLogsConfig.clients().get("tata").endpoint().isEmpty());
    }
}
