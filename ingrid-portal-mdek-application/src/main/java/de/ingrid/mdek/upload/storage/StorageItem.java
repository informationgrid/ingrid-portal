package de.ingrid.mdek.upload.storage;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * StorageItem represents an item in the storage.
 */
public interface StorageItem {
    /**
     * Get the URI
     * @return String
     */
    String getUri();

    /**
     * Get the type
     * @return String
     */
    String getType();

    /**
     * Get the size
     * @return long
     */
    long getSize();

    /**
     * Get an incremented name to be used in case of conflict
     * @return String
     */
    @JsonIgnore
    String getNextName();
}