package de.ingrid.mdek.upload;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class NotAuthorizedException extends WebApplicationException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor
     * @param message
     */
    public NotAuthorizedException(String message) {
        super(message, Response.Status.UNAUTHORIZED.getStatusCode());
    }
}