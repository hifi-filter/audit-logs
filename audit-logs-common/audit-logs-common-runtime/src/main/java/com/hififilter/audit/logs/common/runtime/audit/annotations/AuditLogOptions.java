package com.hififilter.audit.logs.common.runtime.audit.annotations;

import jakarta.ws.rs.NameBinding;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation which can be added to ressource method to customize audit log on it.
 */
@NameBinding
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AuditLogOptions {

    /**
     * Audit log action (human-readable string to group audit logs and allow easier search on it)
     *
     * @return Audit log action (human-readable string to group audit logs and allow easier search on it)
     */
    String action() default "";

    /**
     * Audit log request options used to customize the way we log HTTP request information
     *
     * @return Audit log request options used to customize the way we log HTTP request information
     */
    AuditLogHttpOptions request() default @AuditLogHttpOptions;

    /**
     * Audit log response options used to customize the way we log HTTP request information
     *
     * @return Audit log response options used to customize the way we log HTTP request information
     */
    AuditLogHttpOptions response() default @AuditLogHttpOptions;

}
