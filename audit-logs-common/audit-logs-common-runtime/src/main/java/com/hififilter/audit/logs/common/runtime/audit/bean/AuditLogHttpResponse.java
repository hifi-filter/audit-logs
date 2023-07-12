package com.hififilter.audit.logs.common.runtime.audit.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Log of HTTP response
 */
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuditLogHttpResponse {

    /**
     * HTTP status
     */
    @Getter
    @Setter
    @JsonProperty
    protected int status;

    /**
     * HTTP Headers
     */
    @Getter
    @Setter
    @JsonProperty
    protected Map<String, List<String>> headers;

    /**
     * HTTP Body
     */
    @Getter
    @Setter
    @JsonProperty
    protected Object body;

    /**
     * Custom fields to add in audit log
     */
    @Getter
    @Setter
    @JsonProperty
    protected Map<String, Object> customFields;
}
