package com.hififilter.audit.logs.common.runtime.audit;

import jakarta.enterprise.context.ApplicationScoped;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Factory to get AuditLogCustomFieldsGenerator instance
 */
@ApplicationScoped
public class AuditLogCustomFieldsGeneratorFactory {

    /**
     * Map of custom fields generator instances
     */
    protected final Map<Class<? extends AuditLogCustomFieldsGenerator>, AuditLogCustomFieldsGenerator>
        customFieldsGeneratorInstances = new ConcurrentHashMap<>();

    /**
     * Get AuditLogCustomFieldsGenerator instance
     *
     * @param clazz Class of AuditLogCustomFieldsGenerator
     * @return Instance of AuditLogCustomFieldsGenerator
     */
    public AuditLogCustomFieldsGenerator getInstance(final Class<? extends AuditLogCustomFieldsGenerator> clazz) {
        return customFieldsGeneratorInstances.computeIfAbsent(clazz, c -> {
            var instance = createInstance(c);
            return instance.isPresent() ? instance.get() : new DefaultAuditLogCustomFieldsGenerator();
        });
    }

    /**
     * Create instance from empty constructor
     *
     * @param clazz Class to instantiate
     * @param <T> Type of instance to return
     * @return Instance of clazz
     */
    private <T> Optional<T> createInstance(final Class<T> clazz) {
        try {
            return Optional.of(clazz.getConstructor().newInstance());
        } catch (NoSuchMethodException ex) {
            Loggers.AUDIT_LOGS.warn("{} doesn't have empty constructor", clazz::getName);
            return Optional.empty();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException ex) {
            Loggers.AUDIT_LOGS.warn("Error while instantiation of {}", clazz::getName, () -> ex);
            return Optional.empty();
        }
    }
}
