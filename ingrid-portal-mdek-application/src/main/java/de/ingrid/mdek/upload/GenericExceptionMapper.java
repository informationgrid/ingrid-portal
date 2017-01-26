package de.ingrid.mdek.upload;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.json.JSONException;
import org.json.JSONObject;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {
	@Override
	public Response toResponse(Throwable ex) {
		String resultStr = ex.getMessage();
		String type = MediaType.TEXT_PLAIN;
		try {
			JSONObject result = new JSONObject();
			result.put("success", false);
			result.put("error", ex.getMessage());
			resultStr = result.toString();
			type = MediaType.APPLICATION_JSON;
		} catch (JSONException e) {
			// fall back to plain exception message
		}
	    return Response.status(500).entity(resultStr).type(type).build();
	}
}