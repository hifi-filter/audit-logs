package com.hififilter.audit.logs.client.runtime.audit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Loggers
 */
public final class Loggers {

    /**
     * Main logger
     */
    public static final Logger AUDIT_LOGS_CLIENT = LogManager.getLogger("com.hififilter.auditlogs.client");

    /**
     * Constructor
     */
    private Loggers() {
        // Nothing to do here
    }
}
