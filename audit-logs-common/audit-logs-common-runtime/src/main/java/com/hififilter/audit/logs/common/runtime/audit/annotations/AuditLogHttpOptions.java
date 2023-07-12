package com.hififilter.audit.logs.common.runtime.audit.annotations;

import com.hififilter.audit.logs.common.runtime.audit.AuditLogCustomFieldsGenerator;
import com.hififilter.audit.logs.common.runtime.audit.DefaultAuditLogCustomFieldsGenerator;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation to configure the way we log HTTP request or response.
 *
 * @see AuditLogOptions
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface AuditLogHttpOptions {

    /**
     * Body log enabled or not
     *
     * @return True if body log is enabled, false otherwise
     */
    boolean logEntity() default true;

    /**
     * Class used to generate custom fields on audit log
     *
     * @return Class used to generate custom fields
     */
    Class<? extends AuditLogCustomFieldsGenerator> customFieldGeneratorClass()
        default DefaultAuditLogCustomFieldsGenerator.class;

}
