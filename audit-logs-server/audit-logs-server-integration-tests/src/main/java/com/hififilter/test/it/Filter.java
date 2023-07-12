package com.hififilter.test.it;

import com.hififilter.audit.logs.server.runtime.audit.service.AuditLogServerService;
import jakarta.inject.Inject;
import org.jboss.resteasy.reactive.server.ServerRequestFilter;

/**
 * Add custom field from JAX-RS filter
 */
public class Filter {

    /**
     * Audit log server service
     */
    @Inject
    protected AuditLogServerService auditLogServerService;

    /**
     * Add custom field
     */
    @ServerRequestFilter
    protected void addCustomField() {
        auditLogServerService.putCustomField("foo", "bar");
    }

}
