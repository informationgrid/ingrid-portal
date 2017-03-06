package de.ingrid.mdek.upload;

import java.nio.file.FileAlreadyExistsException;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {
    @Context
    private HttpHeaders headers;

    @Override
    public Response toResponse(Throwable ex) {
        if (ex instanceof FileAlreadyExistsException) {
            ex = new ConflictException("The file already exists.");
        }

        // use response status in case of WebApplicationException
        int status = ex instanceof WebApplicationException ?
                ((WebApplicationException)ex).getResponse().getStatus() :
                    Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();

        // create the upload response (add entity only for JSON)
        UploadResponse response = null;
        List<MediaType> accepts = this.headers.getAcceptableMediaTypes();
        if (accepts.contains(MediaType.APPLICATION_JSON_TYPE)) {
            response = new UploadResponse(ex);
        }
        return Response.status(status).entity(response).build();
    }
}