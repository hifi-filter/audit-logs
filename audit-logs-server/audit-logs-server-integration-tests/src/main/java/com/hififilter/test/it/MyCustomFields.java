package com.hififilter.test.it;

import com.hififilter.audit.logs.common.runtime.audit.AuditLogCustomFieldsGenerator;
import java.util.Map;

/**
 * Custom field generator
 */
public class MyCustomFields implements AuditLogCustomFieldsGenerator {

    @Override
    public Map<String, Object> generate(final Object entity) {
        return Map.of("foo", "bar");
    }
}
