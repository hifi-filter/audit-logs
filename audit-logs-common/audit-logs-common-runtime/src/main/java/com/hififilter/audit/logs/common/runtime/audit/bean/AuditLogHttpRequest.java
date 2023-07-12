package com.hififilter.audit.logs.common.runtime.audit.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

/**
 * Log of HTTP request
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuditLogHttpRequest {

    /**
     * URI
     */
    @Getter
    @Setter
    @JsonProperty
    protected String uri;

    /**
     * HTTP Method (GET, POST, PUT,…)
     */
    @Getter
    @Setter
    @JsonProperty
    protected String method;

    /**
     * HTTP Headers
     */
    @Getter
    @Setter
    @JsonProperty
    protected Map<String, List<String>> headers;

    /**
     * Query params
     */
    @Getter
    @Setter
    @JsonProperty
    protected Map<String, List<String>> queryParams;

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
