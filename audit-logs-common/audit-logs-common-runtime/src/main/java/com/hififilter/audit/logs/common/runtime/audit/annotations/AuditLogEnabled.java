package com.hififilter.audit.logs.common.runtime.audit.annotations;

import jakarta.ws.rs.NameBinding;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to enable audit log on single method.
 *
 * <p>By default this annotation is useless because audit log is enabled on all ressources.
 * It can be used when audit log are disabled on class level and you want to enable it on single method
 * in class.</p>
 *
 * <p>If both annotation @AuditLogDisabled and @AuditLogEnabled are present on the same method. The @AuditLogDisabled
 * take precedence</p>
 *
 * @see AuditLogDisabled
 */
@NameBinding
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AuditLogEnabled {
}
