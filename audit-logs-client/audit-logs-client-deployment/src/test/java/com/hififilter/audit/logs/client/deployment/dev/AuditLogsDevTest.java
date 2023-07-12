package com.hififilter.audit.logs.client.deployment.dev;

import com.hififilter.audit.logs.client.runtime.AuditLogsClientConfig;
import io.quarkus.test.QuarkusDevModeTest;
import jakarta.inject.Inject;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

/**
 * Audit logs extension dev mode test
 */
@Disabled("Injection fail in dev mode 🖕")
public class AuditLogsDevTest {

    /**
     * Register extension
     */
    @RegisterExtension
    protected static final QuarkusDevModeTest DEV_MODE_TEST = new QuarkusDevModeTest()
        .setArchiveProducer(() ->
            ShrinkWrap.create(JavaArchive.class)
                .addAsResource(
                    new StringAsset("""
                        quarkus.hifi-filter.audit-logs.clients.enabled=true
                        quarkus.hifi-filter.audit-logs.clients.endpoint=http://foo.bar
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
    public void testConfig() {
        assertTrue(auditLogsConfig.clientsDefault().endpoint().isPresent());
        assertEquals("http://foo.bar", auditLogsConfig.clientsDefault().endpoint().get());
    }

    @Test
    public void testDisableConfig() {
        DEV_MODE_TEST.modifyResourceFile(
            "application.properties",
            s -> "quarkus.hifi-filter.audit-logs.clients.enabled=false"
        );
        assertFalse(auditLogsConfig.clientsDefault().enabled(), "Audit log should be disabled");
    }

    @Test
    public void testConfigChange() {
        DEV_MODE_TEST.modifyResourceFile(
            "application.properties",
            s -> "quarkus.hifi-filter.audit-logs.clients.endpoint=http://toto.tata"
        );
        assertTrue(auditLogsConfig.clientsDefault().endpoint().isPresent());
        assertEquals("http://toto.tata", auditLogsConfig.clientsDefault().endpoint().get());
    }
}
