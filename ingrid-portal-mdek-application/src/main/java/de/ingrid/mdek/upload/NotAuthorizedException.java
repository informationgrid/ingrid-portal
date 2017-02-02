package de.ingrid.mdek.upload;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class NotAuthorizedException extends WebApplicationException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor
     * @param message
     */
    public NotAuthorizedException(String message) {
        super(Response.status(Response.Status.UNAUTHORIZED)
            .entity(message).type(MediaType.TEXT_PLAIN).build());
    }
}