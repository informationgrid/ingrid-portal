package de.ingrid.mdek.upload;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {
	@Override
	public Response toResponse(Throwable ex) {
	    return Response.status(500).
	    	      entity(ex.getMessage()).
	    	      type("text/plain").
	    	      build();
	}
}