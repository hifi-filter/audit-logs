package com.hififilter.audit.logs.server.runtime.audit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Loggers
 */
public final class Loggers {

    /**
     * Main logger
     */
    public static final Logger AUDIT_LOGS_SERVER = LogManager.getLogger("com.hififilter.auditlogs.server");

    /**
     * Constructor
     */
    private Loggers() {
        // Nothing to do here
    }
}
