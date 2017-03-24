package de.ingrid.mdek.upload;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import de.ingrid.mdek.upload.storage.StorageItem;

public class ConflictException extends WebApplicationException implements UploadException {

    private static final long serialVersionUID = 1L;

    private Map<String, Object> data = new HashMap<String, Object>();

    /**
     * Constructor
     * @param message
     * @param file
     */
    public ConflictException(String message, StorageItem file) {
        super(message, Response.Status.CONFLICT.getStatusCode());
        this.data.put("file", file);
        this.data.put("alt", file.getNextName());
    }

    @Override
    public Map<String, Object> getData() {
        return this.data;
    }
}