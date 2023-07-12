package com.hififilter.audit.logs.server.runtime.audit.exception;

import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

/**
 * RestEasy exception mappers to manage audit log
 */
@ApplicationScoped
public class AuditLogExceptionMappers {

    /**
     * Default exception mapper used to catch unhandled exceptions and go through the normal way with response filter
     *
     * <pre>
     * ⠀⠀⠀⠀⠀⠀⠀⠀⢀⣠⣤⣶⣶⣿⣿⣿⣿⣿⣶⣶⣤⣄⡀⠀⠀⠀⠀⠀⠀⠀
     * ⠀⠀⠀⠀⠀⠀⣠⣶⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣶⣄⠀⠀⠀⠀⠀⠀⠀
     * ⠀⠀⠀⠀⣠⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⡄⠀⠀⠀⠀⠀
     * ⠀⠀⠀⣼⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡏⠀⠀⠙⣿⣿⣿⣿⣿⣆⠀⠀⠀⠀
     * ⠀⠀⣼⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡿⠿⠿⢿⣧⡀⠀⢠⣿⠟⠛⠛⠿⣿⡆⠀⠀⠀
     * ⠀⢰⣿⣿⣿⣿⣿⣿⠿⠟⠋⠉⠁⠀⠀⠀⠀⠀⠙⠿⠿⠟⠋⠀⠀⠀⣠⣿⠇⠀⠀⠀
     * ⠀⢸⣿⣿⡿⠟⠉⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣤⣾⠟⠋⠀⠀⠀⠀
     * ⠀⢸⣿⠋⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣀⣤⣴⣾⠿⠛⠉⠀⠀⠀⠀⠀⠀⠀
     * ⠀⠈⢿⣷⣤⣤⣄⣠⣤⣤⣤⣤⣶⣶⣾⠿⠿⠛⠛⠉⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
     * ⠀⢠⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⣶⣦⣤⣀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
     * ⠀⢸⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⣦⣄⠀⠀⠀⠀⠀⠀
     * ⠀⢸⣿⡛⠿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣦⡀⠀⠀⠀
     * ⠀⠀⢻⣧⠀⠈⠙⠛⠿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡇⠀⠀⠀
     * ⠀⠀⠈⢿⣧⠀⠀⠀⠀⠀⠀⠉⠙⠛⠻⠿⠿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡿⠁⠀⠀⠀
     * ⠀⠀⠀⠀⠻⣷⣄⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠹⣿⣿⣿⣿⠟⠀⣠⣾⠟⠀⠀⠀⠀⠀
     * ⠀⠀⠀⠀⠀⠈⠻⣷⣦⣀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠉⢀⣤⣾⠟⠁⠀⠀⠀⠀⠀⠀
     * ⠀⠀⠀⠀⠀⠀⠀⠀⠙⠻⠿⣶⣦⣤⣤⣤⣤⣤⣤⣶⡿⠟⠋⠁⠀⠀⠀⠀⠀⠀⠀⠀
     * ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⠉⠉⠉⠉⠉⠉⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
     * </pre>
     *
     * @param ex Exception
     * @return A RestEasy RestResponse
     */
    @ServerExceptionMapper
    public RestResponse<String> mapException(final Exception ex) {
        return RestResponse.status(RestResponse.Status.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    /**
     * Default runtime exception mapper used to catch unhandled exceptions and go through the normal way with response
     * filter
     *
     * @param ex Runtime exception
     * @return A RestEasy RestResponse
     */
    @ServerExceptionMapper
    public RestResponse<String> mapException(final RuntimeException ex) {
        return mapException((Exception) ex);
    }
}
