package com.hififilter.audit.logs.common.runtime.audit;

import java.util.Map;

/**
 * Interface used to generate custom fields from output entity
 */
@FunctionalInterface
public interface AuditLogCustomFieldsGenerator {

    /**
     * Generate custom fields from entity
     *
     * @param entity Entity
     * @return Map of custom fields
     */
    Map<String, Object> generate(Object entity);

}
