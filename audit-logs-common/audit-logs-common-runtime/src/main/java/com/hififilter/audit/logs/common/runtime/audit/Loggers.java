package com.hififilter.audit.logs.common.runtime.audit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Loggers
 */
public final class Loggers {

    /**
     * Main logger
     */
    public static final Logger AUDIT_LOGS = LogManager.getLogger("com.hififilter.auditlogs.common");

    /**
     * Constructor
     */
    private Loggers() {
        // Nothing to do here
    }
}
