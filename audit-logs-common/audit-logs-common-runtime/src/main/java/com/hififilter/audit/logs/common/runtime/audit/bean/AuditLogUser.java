package com.hififilter.audit.logs.common.runtime.audit.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Log current user
 */
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuditLogUser {

    /**
     * User ID (email address)
     */
    @Getter
    @Setter
    @JsonProperty
    protected String id;

    /**
     * User remote address (IPv4 address)
     */
    @Getter
    @Setter
    @JsonProperty
    protected String remoteAddress;
}
