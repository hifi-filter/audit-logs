package com.hififilter.audit.logs.server.deployment.dev;

import com.hififilter.audit.logs.server.runtime.AuditLogsServerConfig;
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
                        quarkus.hifi-filter.audit-logs.enabled=true
                        quarkus.hifi-filter.audit-logs.endpoint=http://foo.bar
                    """),
                    "application.properties"
                )
                .addClass(AuditLogsServerConfig.class)
        );

    /**
     * Audit logs extension runtime config
     */
    @Inject
    protected AuditLogsServerConfig auditLogsConfig;

    @Test
    public void testConfig() {
        assertTrue(auditLogsConfig.server().endpoint().isPresent());
        assertEquals("http://foo.bar", auditLogsConfig.server().endpoint().get());
    }

    @Test
    public void testDisableConfig() {
        DEV_MODE_TEST.modifyResourceFile(
            "application.properties",
            s -> "quarkus.hifi-filter.audit-logs.enabled=false"
        );
        assertFalse(auditLogsConfig.server().enabled(), "Audit log should be disabled");
    }

    @Test
    public void testConfigChange() {
        DEV_MODE_TEST.modifyResourceFile(
            "application.properties",
            s -> "quarkus.hifi-filter.audit-logs.endpoint=http://toto.tata"
        );
        assertTrue(auditLogsConfig.server().endpoint().isPresent());
        assertEquals("http://toto.tata", auditLogsConfig.server().endpoint().get());
    }
}
