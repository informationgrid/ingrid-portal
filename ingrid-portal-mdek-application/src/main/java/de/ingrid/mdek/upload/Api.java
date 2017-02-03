package de.ingrid.mdek.upload;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.ingrid.mdek.upload.auth.AuthService;
import de.ingrid.mdek.upload.storage.Action;
import de.ingrid.mdek.upload.storage.Storage;

@Component
@Path("/document")
public class Api {

    private static final String UTF_8 = "UTF-8";

    @Context
    HttpServletRequest request;

    @Autowired
    private Storage storage;

    @Autowired
    private AuthService authService;

    /**
     * Download a document
     *
     * @param path The path and filename of the document
     * @return Response
     * @throws Exception
     */
    @GET
    @Path("{path : [^/]+}/{file : .+}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response download(
            @PathParam("path") String path,
            @PathParam("file") String file) throws Exception {
        // check permission
        if (!this.authService.isAuthorized(this.request, path+"/"+file, Action.READ.name())) {
            throw new NotAuthorizedException("You are not authorized to read the document.");
        }

        // check file existence
        if (!this.storage.exists(path, file)) {
            throw new NotFoundException("The requested document does not exist on the server.");
        }

        // read file
        StreamingOutput fileStream = new StreamingOutput() {
            @Override
            public void write(java.io.OutputStream output) {
                try (InputStream data = Api.this.storage.read(path, file)) {
                    IOUtils.copy(data, output);
                    output.flush();
                }
                catch (IOException ex) {
                    throw new UncheckedIOException(ex);
                }
            }
        };

        // build response
        ResponseBuilder response = Response.ok(fileStream, MediaType.APPLICATION_OCTET_STREAM);
        response.header("Content-Disposition", "attachment; filename=\"" + file + "\"");
        return response.build();
    }

    /**
     * Upload a document or part of a document
     *
     * @param path The path to the document
     * @param file The filename of the document
     * @param id Unique id of the document
     * @param size The size of the document in bytes
     * @param replace Boolean whether to replace an existing document or not (if false, an error will be returned, if
     *            the document exists)
     * @param fileInputStream The file data
     * @param partsTotal The number of parts of the document (only for partial upload)
     * @param partsIndex The index of the current part of the document (only for partial upload)
     * @param partsSize The size of the current part of the document in bytes (only for partial upload)
     * @param partsOffset The byte offset of the current part of the document (only for partial upload)
     * @param uriInfo
     * @return Response
     * @throws Exception
     */
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response upload(
            @FormDataParam("path") String path,
            @FormDataParam("filename") String file,
            @FormDataParam("id") String id,
            @FormDataParam("size") Integer size,
            @FormDataParam("replace") boolean replace,
            @FormDataParam("file") InputStream fileInputStream,
            @FormDataParam("parts_total") Integer partsTotal,
            @FormDataParam("parts_index") Integer partsIndex,
            @FormDataParam("parts_size") Integer partsSize,
            @FormDataParam("parts_offset") Integer partsOffset,
            @Context UriInfo uriInfo) throws Exception {
        // check permission
        if (!this.authService.isAuthorized(this.request, path+"/"+file, Action.CREATE.name())) {
            throw new NotAuthorizedException("You are not authorized to upload the document.");
        }

        // check if file exists already
        if (!replace && this.storage.exists(path, file)) {
            throw new ConflictException("The file already exists.");
        }

        String[] files = new String[0];
        boolean isPartialUpload = partsTotal != null;
        if (isPartialUpload) {
            // store part
            this.storage.writePart(id, partsIndex, fileInputStream, partsSize);
        }
        else {
            // store file
            files = this.storage.write(path, file, fileInputStream, size, replace);
        }
        return this.createUploadResponse(files, uriInfo);
    }

    /**
     * Combine previously uploaded parts of a document
     *
     * @param path The path to the document
     * @param file The filename of the document
     * @param id Unique id of the document
     * @param size The size of the document in bytes
     * @param replace Boolean whether to replace an existing document or not (if false, an error will be returned, if
     *            the document exists)
     * @param partsTotal The number of parts of the document
     * @param uriInfo
     * @return Response
     * @throws Exception
     */
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadPartsCompleted(
            @FormParam("path") String path,
            @FormParam("filename") String file,
            @FormParam("id") String id,
            @FormParam("size") Integer size,
            @FormParam("replace") boolean replace,
            @FormParam("parts_total") Integer partsTotal,
            @Context UriInfo uriInfo) throws Exception {
        // check permission
        if (!this.authService.isAuthorized(this.request, path+"/"+file, Action.CREATE.name())) {
            throw new NotAuthorizedException("You are not authorized to upload the document.");
        }

        // store files
        String[] files = this.storage.combineParts(path, file, id, partsTotal, size, replace);
        return this.createUploadResponse(files, uriInfo);
    }

    /**
     * Delete a document
     *
     * @param path The path and filename of the document
     * @return Response
     * @throws Exception
     */
    @DELETE
    @Path("{path : [^/]+}/{file : .+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(
            @PathParam("path") String path,
            @PathParam("file") String file) throws Exception {
        // check permission
        if (!this.authService.isAuthorized(this.request, path+"/"+file, Action.DELETE.name())) {
            throw new NotAuthorizedException("You are not authorized to delete the document.");
        }

        // check file existence
        if (!this.storage.exists(path, file)) {
            throw new NotFoundException("The requested document does not exist on the server.");
        }

        // delete file
        this.storage.delete(path, file);

        // build response
        return ApiResponse.get(Response.Status.NO_CONTENT).build();
    }

    /**
     * Create the upload response from a list of files
     *
     * @param files
     * @param uriInfo
     * @return Response
     * @throws Exception
     */
    private Response createUploadResponse(String[] files, UriInfo uriInfo) throws Exception {
        // create file URIs
        // NOTE: path method of UriBuilder appends argument, so we can't reuse it
        URI[] fileUris = new URI[files.length];
        for (int i = 0, count = files.length; i < count; i++) {
            fileUris[i] = this.toUri(files[i], uriInfo);
        }
        String createdFile = files.length > 0 ? files[0] : "";
        URI uri = this.toUri(createdFile, uriInfo);

        // build response
        Map<String, URI[]> data = new HashMap<String, URI[]>();
        data.put("files", fileUris);
        return ApiResponse.get(Response.Status.CREATED, data).header("Location", uri).build();
    }

    /**
     * Get the absolute URI of a file
     *
     * @param file
     * @param uriInfo
     * @return URI
     * @throws Exception
     */
    private URI toUri(String file, UriInfo uriInfo) throws Exception {
        return uriInfo.getAbsolutePathBuilder().path(URLEncoder.encode(file, UTF_8).replaceAll("%2F", "/")).build();
    }
}