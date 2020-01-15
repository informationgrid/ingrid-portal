/*-
 * **************************************************-
 * InGrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2020 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
package de.ingrid.mdek.upload;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
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

import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.ingrid.mdek.upload.auth.AuthService;
import de.ingrid.mdek.upload.storage.Action;
import de.ingrid.mdek.upload.storage.Storage;
import de.ingrid.mdek.upload.storage.StorageItem;

@Component
@Path("/document")
public class Api {

    @Context
    HttpServletRequest request;

    @Autowired
    private Storage storage;

    @Autowired
    private AuthService authService;

    /**
     * Get HEAD request and return the Content-Length Header.
     *
     * @param path The path to the document
     * @param file The filename of the document
     * @return Response
     * @throws Exception
     */
    @HEAD
    @Path("{path : .+}/{file : [^/]+}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response head(
            @PathParam("path") final String path,
            @PathParam("file") final String file) throws Exception {
        // check permission
        if (!this.authService.isAuthorized(this.request, path+"/"+file, Action.READ.name())) {
            throw new NotAuthorizedException("You are not authorized to read the document.");
        }

        // check file existence
        if (!this.storage.exists(path, file)) {
            throw new NotFoundException("The requested document does not exist on the server.");
        }

        // build response
        final ResponseBuilder response = Response.ok(MediaType.APPLICATION_OCTET_STREAM);
        response.header("Content-Disposition", "attachment; filename=\"" + file + "\"");
        response.header("Content-Length", this.storage.getInfo(path, file).getSize());
        return response.build();
    }

    /**
     * Download a document
     *
     * @param path The path to the document
     * @param file The filename of the document
     * @return Response
     * @throws Exception
     */
    @GET
    @Path("{path : .+}/{file : [^/]+}")
    @Produces({MediaType.APPLICATION_OCTET_STREAM, MediaType.APPLICATION_JSON})
    public Response download(
          @PathParam("path") final String path,
          @PathParam("file") final String file) throws Exception {
        // check permission
        if (!this.authService.isAuthorized(this.request, path+"/"+file, Action.READ.name())) {
            throw new NotAuthorizedException("You are not authorized to read the document.");
        }

        // check file existence
        if (!this.storage.exists(path, file)) {
            throw new NotFoundException("The requested document does not exist on the server.");
        }

        // read file
        final StreamingOutput fileStream = new StreamingOutput() {
            @Override
            public void write(final java.io.OutputStream output) {
                try (InputStream data = Api.this.storage.read(path, file)) {
                    IOUtils.copy(data, output);
                    output.flush();
                }
                catch (final IOException ex) {
                    throw new UncheckedIOException(ex);
                }
            }
        };

        // build response
        final ResponseBuilder response = Response.ok(fileStream, MediaType.APPLICATION_OCTET_STREAM);
        response.header("Content-Disposition", "attachment; filename=\"" + file + "\"");
        response.header("Content-Length", this.storage.getInfo(path, file).getSize());
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
     * @param extrat Boolean whether to extract archives or not
     * @param fileInputStream The file data
     * @param partsTotal The number of parts of the document (only for partial upload)
     * @param partsIndex The index of the current part of the document (only for partial upload)
     * @param partsSize The size of the current part of the document in bytes (only for partial upload)
     * @param partsOffset The byte offset of the current part of the document (only for partial upload)
     * @return Response
     * @throws Exception
     */
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response upload(
            @FormDataParam("path") final String path,
            @FormDataParam("filename") final String file,
            @FormDataParam("id") final String id,
            @FormDataParam("size") final Integer size,
            @FormDataParam("replace") final boolean replace,
            @FormDataParam("extract") final boolean extract,
            @FormDataParam("file") final InputStream fileInputStream,
            @FormDataParam("parts_total") final Integer partsTotal,
            @FormDataParam("parts_index") final Integer partsIndex,
            @FormDataParam("parts_size") final Integer partsSize,
            @FormDataParam("parts_offset") final Integer partsOffset
            ) throws Exception {
        // check permission
        if (!this.authService.isAuthorized(this.request, path+"/"+file, Action.CREATE.name())) {
            throw new NotAuthorizedException("You are not authorized to upload the document.");
        }

        // check filename
        this.storage.validate(path, file);

        // check if file exists already
        if (!replace && this.storage.exists(path, file)) {
            final StorageItem[] items = { this.storage.getInfo(path, file) };
            throw new ConflictException("The file already exists.", items, items[0].getNextName());
        }

        StorageItem[] files = new StorageItem[0];
        final boolean isPartialUpload = partsTotal != null;
        if (isPartialUpload) {
            // store part
            this.storage.writePart(id, partsIndex, fileInputStream, partsSize);
        }
        else {
            // store file
            files = this.storage.write(path, file, fileInputStream, size, replace, extract);
        }
        return this.createUploadResponse(files);
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
     * @param extrat Boolean whether to extract archives or not
     * @param partsTotal The number of parts of the document
     * @return Response
     * @throws Exception
     */
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadPartsCompleted(
            @FormParam("path") final String path,
            @FormParam("filename") final String file,
            @FormParam("id") final String id,
            @FormParam("size") final Integer size,
            @FormParam("replace") final boolean replace,
            @FormParam("extract") final boolean extract,
            @FormParam("parts_total") final Integer partsTotal
            ) throws Exception {
        // check permission
        if (!this.authService.isAuthorized(this.request, path+"/"+file, Action.CREATE.name())) {
            throw new NotAuthorizedException("You are not authorized to upload the document.");
        }

        // check filename
        this.storage.validate(path, file);

        // store files
        final StorageItem[] files = this.storage.combineParts(path, file, id, partsTotal, size, replace, extract);
        return this.createUploadResponse(files);
    }

    /**
     * Delete a document
     *
     * @param path The path to the document
     * @param file The filename of the document
     * @return Response
     * @throws Exception
     */
    @DELETE
    @Path("{path : .+}/{file : [^/]+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(
            @PathParam("path") final String path,
            @PathParam("file") final String file) throws Exception {
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
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    /**
     * Create the upload response from a list of files
     *
     * @param files
     * @return Response
     * @throws Exception
     */
    private Response createUploadResponse(final StorageItem[] files) throws Exception {
        // get first URI for location header
        final String createdFile = files.length > 0 ? files[0].getUri() : "";

        // build response
        final UploadResponse uploadResponse = new UploadResponse(Arrays.asList(files));
        return Response.created(new URI(createdFile)).entity(uploadResponse).build();
    }
}
