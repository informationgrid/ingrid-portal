package de.ingrid.mdek.upload;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URI;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
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
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.ingrid.mdek.upload.auth.AuthService;
import de.ingrid.mdek.upload.storage.Action;
import de.ingrid.mdek.upload.storage.Storage;

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
	 * Download a document
	 * @param path The path and filename of the document
	 * @return Response
	 * @throws Exception
	 */
	@GET
	@Path("{path : .+}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response download(@PathParam("path") String path) throws Exception {
		// check permission
		if (!authService.isAuthorized(request, path, Action.READ.name())) {
			return Response.status(403).entity("You are not authorized to read the document.").build();
		}
		
		// check file existence
		if (!storage.exists(path)) {
			return Response.status(404).entity("The requested document does not exist on the server.").build();
		}

		// read file
		StreamingOutput fileStream = new StreamingOutput() {
			@Override
			public void write(java.io.OutputStream output) {
					try (InputStream data = storage.read(path)) {
						IOUtils.copy(data, output);
						output.flush();
					} catch (IOException ex) {
						throw new UncheckedIOException(ex);
					}
			}
		};

		// build response
		ResponseBuilder response = Response.ok(fileStream, MediaType.APPLICATION_OCTET_STREAM);
		response.header("Content-Disposition", "attachment; filename=\""+path+"\"");
		return response.build();
	}
	
	/**
	 * Upload a document or part of a document
	 * @param path The path to the document
	 * @param fileName The filename of the document
	 * @param id Unique id of the document
	 * @param size The size of the document in bytes
	 * @param replace Boolean whether to replace an existing document or not (if false, an error will be returned, if the document exists)
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
			@FormDataParam("filename") String fileName,
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
		if (!authService.isAuthorized(request, fileName, Action.CREATE.name())) {
			return Response.status(403).entity("You are not authorized to upload the document.").build();
		}
		
		String[] files = new String[0];
		boolean isPartialUpload = partsTotal != null;
		if (isPartialUpload) {
			// store part
			storage.writePart(id, partsIndex, fileInputStream, partsSize);
		}
		else {
			// store file
			files = storage.write(path, fileName, fileInputStream, size, replace);
		}
		return createUploadResponse(files, uriInfo);
	}

	/**
	 * Combine previously uploaded parts of a document
	 * @param path The path to the document
	 * @param fileName The filename of the document
	 * @param id Unique id of the document
	 * @param size The size of the document in bytes
	 * @param replace Boolean whether to replace an existing document or not (if false, an error will be returned, if the document exists)
	 * @param partsTotal The number of parts of the document
	 * @param uriInfo
	 * @return Response
	 * @throws Exception
	 */
	@POST
	@Path("/done")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response uploadPartsCompleted(
			@FormParam("path") String path,
			@FormParam("filename") String fileName,
			@FormParam("id") String id,
			@FormParam("size") Integer size,
			@FormParam("replace") boolean replace,
			@FormParam("parts_total") Integer partsTotal,
			@Context UriInfo uriInfo) throws Exception {
		// check permission
		if (!authService.isAuthorized(request, fileName, Action.CREATE.name())) {
			return Response.status(403).entity("You are not authorized to upload the document.").build();
		}

		// store files
		String[] files = storage.combineParts(path, fileName, id, partsTotal, size, replace);
		return createUploadResponse(files, uriInfo);
	}

	/**
	 * Delete a document
	 * @param path The path and filename of the document
	 * @return Response
	 * @throws Exception
	 */
	@DELETE
	@Path("{path : .+}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(@PathParam("path") String path) throws Exception {
		// check permission
		if (!authService.isAuthorized(request, path, Action.DELETE.name())) {
			return Response.status(403).entity("You are not authorized to delete the document.").build();
		}
		
		// check file existence
		if (!storage.exists(path)) {
			return Response.status(404).entity("The requested document does not exist on the server.").build();
		}

		// delete file
		storage.delete(path);

		// build response
		JSONObject result = new JSONObject();
		result.put("success", true);
		return Response.status(204).entity(result.toString()).build();
	}

	/**
	 * Create the upload response from a list of files
	 * @param files
	 * @param uriInfo
	 * @return Response
	 * @throws JSONException
	 */
	private Response createUploadResponse(String[] files, UriInfo uriInfo) throws JSONException {
		// create file URIs
		// NOTE: path method of UriBuilder appends argument, so we can't reuse it
		URI[] fileUris = new URI[files.length];
		for (int i = 0, count = files.length; i < count; i++) {
			fileUris[i] = uriInfo.getAbsolutePathBuilder().path(files[i]).build();
		}
		String createdFile = files.length > 0 ? files[0] : "";
		URI uri = uriInfo.getAbsolutePathBuilder().path(createdFile).build();

		// build response
		JSONObject result = new JSONObject();
		result.put("success", true);
		result.put("files", fileUris);
		return Response.created(uri).entity(result.toString()).build();
	}
}