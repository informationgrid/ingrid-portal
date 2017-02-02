package de.ingrid.mdek.upload;

import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.json.JSONException;
import org.json.JSONObject;

public class ApiResponse {

    private ResponseBuilder builder = null;

    private Integer status = Response.Status.OK.getStatusCode();

    private Throwable ex = null;
    private Map<String, ?> data;

    /**
     * Constructor
     * @param status The response status
     */
    private ApiResponse(Response.Status status) {
        this.status = status.getStatusCode();
        this.builder = Response.status(this.status);
    }

    /**
     * Get a response builder from a response status
     * @param status The response status
     */
    public static ResponseBuilder get(Response.Status status) {
        return new ApiResponse(status).build();
    }

    /**
     * Get a response builder from a response status and data
     * @param status The response status
     * @param data Map of additional response data
     */
    public static ResponseBuilder get(Response.Status status, Map<String,?> data) {
        ApiResponse response = new ApiResponse(status);
        response.data = data;
        return response.build();
    }

    /**
     * Get a response builder from an exception
     * @param ex The exception
     */
    public static ResponseBuilder get(Throwable ex) {
        ApiResponse response = new ApiResponse(Response.Status.INTERNAL_SERVER_ERROR);
        response.ex = ex;
        return response.build();
    }

    /**
     * Get a response builder from an exception
     * @param ex The exception
     */
    public static ResponseBuilder get(WebApplicationException ex) {
        ApiResponse response = new ApiResponse(Response.Status.fromStatusCode(ex.getResponse().getStatus()));
        response.ex = ex;
        return response.build();
    }

    /**
     * Add the common response content
     * @return ResponseBuilder
     */
    private ResponseBuilder build() {
        // create response content
        String resultStr = null;
        String resultType = MediaType.TEXT_PLAIN;
        try {
            JSONObject result = new JSONObject();
            if (this.ex == null) {
                result.put("success", true);
            }
            else {
                result.put("success", false);
                result.put("error", this.ex.getMessage());
            }
            for (Entry<String, ?> item : this.data.entrySet()) {
                result.put(item.getKey(), item.getValue());
            }
            resultStr = result.toString();
            resultType = MediaType.APPLICATION_JSON;
        }
        catch (JSONException e) {
            // fall back to plain exception message
        }

        // build the response
        this.builder.type(resultType);
        if (resultStr != null) {
            this.builder.entity(resultStr);
        }
        return this.builder;

    }
}
