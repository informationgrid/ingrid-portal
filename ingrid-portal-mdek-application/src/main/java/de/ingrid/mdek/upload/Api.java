package de.ingrid.mdek.upload;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URI;

import javax.ws.rs.Consumes;
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

import de.ingrid.mdek.upload.storage.Storage;

@Path("/document")
@Component
public class Api {

	@Autowired
	private Storage storage;

	@GET
	@Path("{path : .+}")
	public Response download(@PathParam("path") String path) throws Exception {
		// read file
		final File file = new File(path);
		StreamingOutput fileStream = new StreamingOutput() {
			@Override
			public void write(java.io.OutputStream output) {
					try (InputStream data = storage.read(file)) {
						IOUtils.copy(data, output);
						output.flush();
					} catch (IOException ex) {
						throw new UncheckedIOException(ex);
					}
			}
		};

		// build response
		ResponseBuilder response = Response.ok(fileStream, MediaType.APPLICATION_OCTET_STREAM);
		response.header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
		return response.build();
	}

	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response upload(@FormDataParam("title") String title, @FormDataParam("filename") String fileName,
			@FormDataParam("replace") boolean replace, @FormDataParam("file") InputStream fileInputStream,
			@FormDataParam("file") FormDataContentDisposition contentDispositionHeader, @Context UriInfo uriInfo)
			throws Exception {
		// store files
		String finalName = fileName != null ? fileName : contentDispositionHeader.getFileName();
		File[] files = storage.write(new File(title, finalName), fileInputStream, replace);

		// create file URIs
		// NOTE: path method of UriBuilder appends argument, so we can't reuse
		// it
		URI[] fileUris = new URI[files.length];
		for (int i = 0, count = files.length; i < count; i++) {
			fileUris[i] = uriInfo.getAbsolutePathBuilder().path(files[i].getPath()).build();
		}
		File createdFile = files.length == 1 ? files[0] : files[0].getParentFile();
		URI uri = uriInfo.getAbsolutePathBuilder().path(createdFile.getPath()).build();

		// build response
		JSONObject result = new JSONObject();
		result.put("success", true);
		result.put("files", fileUris);
		return Response.created(uri).entity(result.toString()).build();
	}
}