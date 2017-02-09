package de.ingrid.mdek.upload;

import java.util.List;

import de.ingrid.mdek.upload.storage.Item;

public class UploadResponse {

    private boolean success = true;
    private String error = null;
    private List<Item> files = null;

    /**
     * Success constructor
     */
    public UploadResponse() {}

    /**
     * Success constructor with uploaded files
     * @param files File items
     */
    public UploadResponse(List<Item> files) {
        this.files = files;
    }

    /**
     * Error constructor
     * @param ex The exception
     */
    public UploadResponse(Throwable ex) {
        this.success = false;
        this.error = ex.getMessage();
    }

    /**
     * Set the status property
     * @param success
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * Get the success property
     * @return boolean
     */
    public boolean getSuccess() {
        return this.success;
    }

    /**
     * Set the error text
     * @param error
     */
    public void setError(String error) {
        this.error = error;
    }

    /**
     * Get the error text
     * @return String
     */
    public String getError() {
        return this.error;
    }

    /**
     * Set the files
     * @param files
     */
    public void setFiles(List<Item> files) {
        this.files = files;
    }

    /**
     * Get the files
     * @return List<Item>
     */
    public List<Item> getFiles() {
        return this.files;
    }
}
