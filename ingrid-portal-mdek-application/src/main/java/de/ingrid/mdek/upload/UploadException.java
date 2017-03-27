package de.ingrid.mdek.upload;

import java.util.Map;

public interface UploadException {

    /**
     * Get error data
     * @return Map<String, Object>
     */
    Map<String, Object>getData();
}
