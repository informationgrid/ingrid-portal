package de.ingrid.mdek.upload.storage.validate;

import java.nio.file.Path;
import java.util.Map;

import de.ingrid.mdek.upload.ValidationException;

/**
 * Validator instances are used to validate files before adding them to the storage
 */
public interface Validator {
    /**
     * Initialization method called with configuration values
     * @param properties
     * @throws IllegalArgumentException
     */
    void initialize(Map<String, String> configuration) throws IllegalArgumentException;

    /**
     * Validate the path, file and data
     * NOTE: Since this method could be called in different stages of the upload process one or more parameters might be null.
     * E.g. in an early stage only the file path and name could be validated while the file data is still unknown.
     * @param path
     * @param file
     * @param data
     */
    void validate(final String path, final String file, Path data) throws ValidationException;
}
