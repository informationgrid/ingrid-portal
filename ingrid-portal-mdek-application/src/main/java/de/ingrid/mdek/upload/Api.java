package de.ingrid.mdek.upload;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URI;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.ingrid.mdek.upload.auth.AuthService;
import de.ingrid.mdek.upload.storage.Action;
import de.ingrid.mdek.upload.storage.Storage;

@Path("/document")
@Component
public class Api {

    @Context
    HttpServletRequest request;
	
	@Autowired
	private Storage storage;

	@Autowired
	private AuthService authService;

	@GET
	@Path("{path : .+}")
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

	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response upload(@FormDataParam("title") String title,
			@FormDataParam("filename") String fileName,
			@FormDataParam("replace") boolean replace,
			@FormDataParam("file") InputStream fileInputStream,
			@FormDataParam("file") FormDataContentDisposition contentDispositionHeader,
			@Context UriInfo uriInfo) throws Exception {
		// check permission
		String finalName = fileName != null ? fileName : contentDispositionHeader.getFileName();
		if (!authService.isAuthorized(request, fileName, Action.CREATE.name())) {
			return Response.status(403).entity("You are not authorized to upload the document.").build();
		}

		// store files
		String[] files = storage.write(title, finalName, fileInputStream, replace);

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

	@DELETE
	@Path("{path : .+}")
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
		return Response.status(204).build();
	}
}