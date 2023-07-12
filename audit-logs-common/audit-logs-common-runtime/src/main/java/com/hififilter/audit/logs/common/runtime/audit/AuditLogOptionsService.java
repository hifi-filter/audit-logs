package com.hififilter.audit.logs.common.runtime.audit;

import com.hififilter.audit.logs.common.runtime.audit.annotations.AuditLogDisabled;
import com.hififilter.audit.logs.common.runtime.audit.annotations.AuditLogEnabled;
import com.hififilter.audit.logs.common.runtime.audit.annotations.AuditLogOptions;
import jakarta.enterprise.context.ApplicationScoped;
import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Utils to handle annotations
 */
@ApplicationScoped
public class AuditLogOptionsService {

    /**
     * Check if audit log is disabled on the current method.<br />
     *
     * <p>Audit log is disabled if AuditLogDisabled is present on method or parent class</p>
     *
     * @param invokedMethod Invoked method
     * @return True if audit log is disabled
     */
    public boolean isDisabled(final Method invokedMethod) {
        var disabledOnClass = invokedMethod.getDeclaringClass().getAnnotation(AuditLogDisabled.class) != null;
        var disabledOnMethod = invokedMethod.getAnnotation(AuditLogDisabled.class) != null;
        var enabledOnMethod = invokedMethod.getAnnotation(AuditLogEnabled.class) != null;

        return disabledOnMethod || disabledOnClass && !enabledOnMethod;
    }

    /**
     * Get audit log options for an invoked method
     *
     * @param invokedMethod Invoked method
     * @return Options for current method
     */
    public AuditLogOptions getOptions(final Method invokedMethod) {
        return Optional.ofNullable(invokedMethod.getAnnotation(AuditLogOptions.class))
            .orElseGet(() -> {
                try {
                    return getClass().getDeclaredMethod("holderMethod").getAnnotation(AuditLogOptions.class);
                } catch (NoSuchMethodException ex) {
                    throw new IllegalStateException("holderMethod method doesn't exists in " + getClass().getName());
                }
            });
    }

    /**
     * Simple method which do nothing but hold an annotation implementation of AuditLogOptions
     */
    @AuditLogOptions
    private void holderMethod() {
    }

}
