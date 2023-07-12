package com.hififilter.audit.logs.common.runtime;

import java.util.concurrent.atomic.LongAdder;

/**
 * Audit log metrics enum
 */
public final class AuditLogsMetrics {

    /**
     * Send success
     */
    public static final String SEND_SUCCESS = "hifi-filter.audit-logs.send.success";

    /**
     * Send failed
     */
    public static final String SEND_FAILED = "hifi-filter.audit-logs.send.failed";

    /**
     * Send success counter
     */
    public static final LongAdder SEND_SUCCESS_COUNTER = new LongAdder();

    /**
     * Send failed counter
     */
    public static final LongAdder SEND_FAILED_COUNTER = new LongAdder();

    /**
     * Constructor
     */
    private AuditLogsMetrics() {
    }
}
