package com.hififilter.audit.logs.common.runtime.audit;

import com.hififilter.audit.logs.common.runtime.audit.bean.AuditLog;
import io.smallrye.mutiny.Uni;

/**
 * Interface to implement an audit log sender
 */
@FunctionalInterface
public interface AuditLogSender {

    /**
     * Send audit log.
     *
     * @param auditLog Audit log to send
     * @return void
     */
    Uni<Void> send(final AuditLog auditLog);
}
