package de.ingrid.mdek.upload;

import java.nio.file.FileAlreadyExistsException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {
    @Override
    public Response toResponse(Throwable ex) {
        if (ex instanceof FileAlreadyExistsException) {
            ex = new ConflictException("The file already exists.");
        }
        return ApiResponse.get(ex).build();
    }
}