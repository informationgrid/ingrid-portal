package de.ingrid.mdek.upload.storage.validate;

import de.ingrid.mdek.upload.ValidationException;

/**
 * Exception signaling a virus infection. The HTTP status code is 419.
 */
public class VirusFoundException extends ValidationException {

    private static final int STATUS_CODE = 419;

    private static final long serialVersionUID = 1L;

    public VirusFoundException(final String message, final String file) {
        super(message, file, STATUS_CODE);
    }
}
