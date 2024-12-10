package com.unisys.config;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class SecurityExceptionMapper implements ExceptionMapper<SecurityException> {

    @Override
    public Response toResponse(SecurityException exception) {
        // Log the error for debugging or monitoring
        return Response.status(Response.Status.FORBIDDEN)
                       .entity(exception.getMessage())  // Custom message from SecurityException
                       .build();
    }
}
