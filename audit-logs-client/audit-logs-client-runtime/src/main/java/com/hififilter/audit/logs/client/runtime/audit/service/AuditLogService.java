package com.hififilter.audit.logs.client.runtime.audit.service;

import com.hififilter.audit.logs.client.runtime.audit.AuditLogClientSender;
import com.hififilter.audit.logs.client.runtime.audit.annotations.ClientSender;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Audit log server service
 */
@ApplicationScoped
public class AuditLogService extends com.hififilter.audit.logs.common.runtime.audit.service.AuditLogService {

    /**
     * Constructor for CDI
     */
    public AuditLogService() {
        super(null);
    }

    /**
     * Init audit log with request infos.
     *
     * @param senderService Audit log sender service
     */
    @Inject
    public AuditLogService(@ClientSender final AuditLogClientSender senderService) {
        super(senderService);
    }
}