package com.hififilter.audit.logs.common.runtime.audit.annotations;

import jakarta.ws.rs.NameBinding;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to disable audit log on single method or in complete class.
 *
 * <p>If audit log is disabled on class level, you can use @AuditLogEnabled on single method
 * to override this behavior.</p>
 *
 * @see AuditLogEnabled
 */
@NameBinding
@Inherited
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AuditLogDisabled {
}
